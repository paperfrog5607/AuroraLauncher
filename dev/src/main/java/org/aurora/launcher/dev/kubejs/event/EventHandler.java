package org.aurora.launcher.dev.kubejs.event;

public class EventHandler {

    public String generateEventScript(EventTemplate template, EventConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("// ").append(template.getName()).append("\n");
        
        String eventTypeStr = getEventTypeString(template.getEventType());
        
        if (config.getFilter() != null && !config.getFilter().isEmpty()) {
            sb.append("ServerEvents.").append(eventTypeStr).append("('").append(config.getFilter()).append("', event => {\n");
        } else {
            sb.append("ServerEvents.").append(eventTypeStr).append("(event => {\n");
        }
        
        if (config.getLogic() != null) {
            sb.append(config.getLogic()).append("\n");
        }
        
        sb.append("});");
        return sb.toString();
    }

    private String getEventTypeString(EventType type) {
        switch (type) {
            case BLOCK_BREAK: return "blockBroken";
            case BLOCK_PLACE: return "blockPlace";
            case ITEM_CRAFTED: return "itemCrafted";
            case ITEM_SMELTED: return "itemSmelted";
            case ENTITY_DEATH: return "entityDeath";
            case PLAYER_JOIN: return "playerJoin";
            case PLAYER_QUIT: return "playerQuit";
            case PLAYER_CHAT: return "playerChat";
            case SERVER_LOAD: return "serverLoad";
            case SERVER_TICK: return "serverTick";
            case WORLD_LOAD: return "worldLoad";
            case WORLD_TICK: return "worldTick";
            default: return type.name().toLowerCase();
        }
    }
}