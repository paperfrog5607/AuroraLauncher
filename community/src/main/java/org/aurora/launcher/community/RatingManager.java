package org.aurora.launcher.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class RatingManager {

    private static final Logger logger = LoggerFactory.getLogger(RatingManager.class);
    private static RatingManager instance;

    private final Map<String, PackageRating> userRatings;
    private final String authToken;

    private RatingManager(String authToken) {
        this.userRatings = new ConcurrentHashMap<>();
        this.authToken = authToken;
    }

    public static synchronized RatingManager getInstance(String authToken) {
        if (instance == null) {
            instance = new RatingManager(authToken);
        }
        return instance;
    }

    public void ratePackage(String packageId, int rating, String comment, RatingCallback callback) {
        if (rating < 1 || rating > 5) {
            callback.onError("Rating must be between 1 and 5");
            return;
        }

        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/packages/" + packageId + "/reviews";
                String json = String.format(
                    "{\"rating\":%d,\"content\":\"%s\"}", rating, comment.replace("\"", "\\\""));
                
                String response = sendPost(url, json, authToken);
                
                PackageRating pkgRating = new PackageRating();
                pkgRating.packageId = packageId;
                pkgRating.rating = rating;
                pkgRating.comment = comment;
                pkgRating.timestamp = System.currentTimeMillis();
                
                userRatings.put(packageId, pkgRating);
                saveRatings();
                
                callback.onSuccess(pkgRating);
            } catch (Exception e) {
                logger.error("Rating failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getPackageRatings(String packageId, RatingsCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/packages/" + packageId + "/reviews";
                String response = sendGet(url, authToken);
                
                List<Review> reviews = parseReviews(response);
                double averageRating = reviews.stream()
                    .mapToInt(r -> r.rating)
                    .average()
                    .orElse(0.0);
                
                callback.onSuccess(reviews, averageRating);
            } catch (Exception e) {
                logger.error("Failed to get ratings", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getTopRated(String type, int limit, PackagesCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/packages?sort=rating&limit=" + limit + 
                    (type != null ? "&type=" + type : "");
                String response = sendGet(url, authToken);
                
                List<PackageSummary> packages = parsePackageList(response);
                callback.onSuccess(packages);
            } catch (Exception e) {
                logger.error("Failed to get top rated", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void getMostDownloaded(String type, int limit, PackagesCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/packages?sort=downloads&limit=" + limit +
                    (type != null ? "&type=" + type : "");
                String response = sendGet(url, authToken);
                
                List<PackageSummary> packages = parsePackageList(response);
                callback.onSuccess(packages);
            } catch (Exception e) {
                logger.error("Failed to get most downloaded", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void searchPackages(String query, String type, int limit, PackagesCallback callback) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/packages?search=" + 
                    java.net.URLEncoder.encode(query, "UTF-8") +
                    (type != null ? "&type=" + type : "") +
                    "&limit=" + limit;
                String response = sendGet(url, authToken);
                
                List<PackageSummary> packages = parsePackageList(response);
                callback.onSuccess(packages);
            } catch (Exception e) {
                logger.error("Failed to search packages", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public PackageRating getUserRating(String packageId) {
        return userRatings.get(packageId);
    }

    private void saveRatings() {
        Path ratingFile = Paths.get(System.getProperty("user.home"), ".aurora", "ratings.dat");
        try {
            Files.createDirectories(ratingFile.getParent());
            try (DataOutputStream dos = new DataOutputStream(
                    new FileOutputStream(ratingFile.toFile()))) {
                dos.writeInt(userRatings.size());
                for (Map.Entry<String, PackageRating> entry : userRatings.entrySet()) {
                    dos.writeUTF(entry.getKey());
                    dos.writeInt(entry.getValue().rating);
                    dos.writeUTF(entry.getValue().comment != null ? entry.getValue().comment : "");
                    dos.writeLong(entry.getValue().timestamp);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to save ratings", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRatings() {
        Path ratingFile = Paths.get(System.getProperty("user.home"), ".aurora", "ratings.dat");
        if (!Files.exists(ratingFile)) return;
        
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(ratingFile.toFile()))) {
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                String packageId = dis.readUTF();
                PackageRating rating = new PackageRating();
                rating.packageId = packageId;
                rating.rating = dis.readInt();
                rating.comment = dis.readUTF();
                rating.timestamp = dis.readLong();
                userRatings.put(packageId, rating);
            }
        } catch (IOException e) {
            logger.error("Failed to load ratings", e);
        }
    }

    private List<Review> parseReviews(String json) {
        List<Review> reviews = new ArrayList<>();
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "\\{\"id\":\"([^\"]+)\",\"rating\":(\\d+),\"content\":\"([^\"]*)\"[^}]*\\}");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        
        while (matcher.find()) {
            Review review = new Review();
            review.id = matcher.group(1);
            review.rating = Integer.parseInt(matcher.group(2));
            review.comment = matcher.group(3);
            reviews.add(review);
        }
        
        return reviews;
    }

    private List<PackageSummary> parsePackageList(String json) {
        List<PackageSummary> packages = new ArrayList<>();
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "\\{\"id\":\"([^\"]+)\",\"name\":\"([^\"]+)\",\"rating\":([\\d.]+),\"downloads\":(\\d+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(json);
        
        while (matcher.find()) {
            PackageSummary pkg = new PackageSummary();
            pkg.id = matcher.group(1);
            pkg.name = matcher.group(2);
            pkg.rating = Double.parseDouble(matcher.group(3));
            pkg.downloads = Integer.parseInt(matcher.group(4));
            packages.add(pkg);
        }
        
        return packages;
    }

    private String sendPost(String url, String json, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setDoOutput(true);
        try (java.io.OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        return new String(conn.getInputStream().readAllBytes());
    }

    private String sendGet(String url, String token) throws IOException {
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        return new String(conn.getInputStream().readAllBytes());
    }

    public static class PackageRating {
        public String packageId;
        public int rating;
        public String comment;
        public long timestamp;
    }

    public static class Review {
        public String id;
        public int rating;
        public String comment;
    }

    public static class PackageSummary {
        public String id;
        public String name;
        public double rating;
        public int downloads;
    }

    public interface RatingCallback {
        void onSuccess(PackageRating rating);
        void onError(String error);
    }

    public interface RatingsCallback {
        void onSuccess(List<Review> reviews, double averageRating);
        void onError(String error);
    }

    public interface PackagesCallback {
        void onSuccess(List<PackageSummary> packages);
        void onError(String error);
    }
}