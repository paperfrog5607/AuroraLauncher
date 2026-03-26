package org.aurora.launcher.network.p2p;

import java.time.Instant;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class IceService {
    
    private static final String[] STUN_SERVERS = {
        "stun:stun.l.google.com:19302",
        "stun:stun1.l.google.com:19302"
    };
    
    private NatType detectedNatType;
    private String localAddress;
    private String publicAddress;
    private int publicPort;
    private boolean isRunning;
    private final CopyOnWriteArrayList<Consumer<NatType>> natTypeListeners;
    private final CopyOnWriteArrayList<Consumer<IceCandidate>> candidateListeners;

    public IceService() {
        this.natTypeListeners = new CopyOnWriteArrayList<>();
        this.candidateListeners = new CopyOnWriteArrayList<>();
        this.detectedNatType = NatType.UNKNOWN;
    }

    public void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        detectNatType();
    }

    public void stop() {
        isRunning = false;
    }

    private void detectNatType() {
        new Thread(() -> {
            try {
                String stunServer = STUN_SERVERS[0].replace("stun:", "").split(":")[0];
                int stunPort = Integer.parseInt(STUN_SERVERS[0].split(":")[2]);
                
                StunClient stunClient = new StunClient(stunServer, stunPort);
                StunResult result = stunClient.discover();
                
                if (result != null) {
                    this.publicAddress = result.getPublicIp();
                    this.publicPort = result.getPublicPort();
                    this.localAddress = result.getLocalIp();
                    this.detectedNatType = result.getNatType();
                } else {
                    this.detectedNatType = NatType.UNKNOWN;
                }
                
                notifyNatTypeChanged();
                
            } catch (Exception e) {
                this.detectedNatType = NatType.UNKNOWN;
                notifyNatTypeChanged();
            }
        }).start();
    }

    public NatType getNatType() {
        return detectedNatType;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public String getPublicAddress() {
        return publicAddress;
    }

    public int getPublicPort() {
        return publicPort;
    }

    public void addNatTypeListener(Consumer<NatType> listener) {
        natTypeListeners.add(listener);
    }

    public void removeNatTypeListener(Consumer<NatType> listener) {
        natTypeListeners.remove(listener);
    }

    public void addCandidateListener(Consumer<IceCandidate> listener) {
        candidateListeners.add(listener);
    }

    public void removeCandidateListener(Consumer<IceCandidate> listener) {
        candidateListeners.remove(listener);
    }

    private void notifyNatTypeChanged() {
        natTypeListeners.forEach(l -> l.accept(detectedNatType));
    }

    protected void notifyCandidate(IceCandidate candidate) {
        candidateListeners.forEach(l -> l.accept(candidate));
    }

    public static class IceCandidate {
        private final String type;
        private final String address;
        private final int port;
        private final Instant timestamp;

        public IceCandidate(String type, String address, int port) {
            this.type = type;
            this.address = address;
            this.port = port;
            this.timestamp = Instant.now();
        }

        public String getType() {
            return type;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return type + ":" + address + ":" + port;
        }
    }

    private static class StunClient {
        private final String server;
        private final int port;

        public StunClient(String server, int port) {
            this.server = server;
            this.port = port;
        }

        public StunResult discover() {
            try (var socket = new java.net.DatagramSocket()) {
                socket.setSoTimeout(5000);
                
                byte[] transactionId = generateTransactionId();
                byte[] request = buildBindingRequest(transactionId);
                
                java.net.InetAddress address = java.net.InetAddress.getByName(server);
                java.net.DatagramPacket packet = new java.net.DatagramPacket(
                        request, request.length, address, port);
                
                socket.send(packet);
                
                byte[] response = new byte[1024];
                java.net.DatagramPacket receivePacket = new java.net.DatagramPacket(
                        response, response.length);
                
                socket.receive(receivePacket);
                
                return parseBindingResponse(response, socket.getLocalAddress().getHostAddress());
                
            } catch (Exception e) {
                return null;
            }
        }

        private byte[] generateTransactionId() {
            byte[] id = new byte[12];
            new java.security.SecureRandom().nextBytes(id);
            return id;
        }

        private byte[] buildBindingRequest(byte[] transactionId) {
            byte[] header = new byte[20];
            header[0] = 0x00;
            header[1] = 0x01;
            byte[] length = new byte[] {0x00, 0x00};
            header[2] = length[0];
            header[3] = length[1];
            byte[] magic = new byte[] {0x21, 0x12, (byte)0xA4, 0x42};
            System.arraycopy(magic, 0, header, 4, 4);
            System.arraycopy(transactionId, 0, header, 4, 12);
            return header;
        }

        private StunResult parseBindingResponse(byte[] data, String localIp) {
            if (data.length < 20) {
                return null;
            }
            
            int mappedAddress = 0;
            int mappedPort = 0;
            
            for (int i = 20; i < data.length - 8; ) {
                int type = (data[i] << 8) | (data[i + 1] & 0xFF);
                int len = (data[i + 2] << 8) | (data[i + 3] & 0xFF);
                
                if (type == 0x0020 && len >= 8) {
                    byte[] addr = new byte[4];
                    System.arraycopy(data, i + 8, addr, 0, 4);
                    mappedAddress = ((addr[0] & 0xFF) << 24) | 
                                   ((addr[1] & 0xFF) << 16) | 
                                   ((addr[2] & 0xFF) << 8) | 
                                   (addr[3] & 0xFF);
                    mappedPort = ((data[i + 10] & 0xFF) << 8) | (data[i + 11] & 0xFF);
                    break;
                }
                
                i += 4 + len + (len % 4 == 0 ? 0 : 4 - (len % 4));
            }
            
            String publicIp = String.format("%d.%d.%d.%d",
                    (mappedAddress >> 24) & 0xFF,
                    (mappedAddress >> 16) & 0xFF,
                    (mappedAddress >> 8) & 0xFF,
                    mappedAddress & 0xFF);
            
            NatType natType = NatType.MODERATE;
            
            return new StunResult(localIp, publicIp, mappedPort, natType);
        }
    }

    private static class StunResult {
        private final String localIp;
        private final String publicIp;
        private final int publicPort;
        private final NatType natType;

        public StunResult(String localIp, String publicIp, int publicPort, NatType natType) {
            this.localIp = localIp;
            this.publicIp = publicIp;
            this.publicPort = publicPort;
            this.natType = natType;
        }

        public String getLocalIp() {
            return localIp;
        }

        public String getPublicIp() {
            return publicIp;
        }

        public int getPublicPort() {
            return publicPort;
        }

        public NatType getNatType() {
            return natType;
        }
    }
}
