# 模块十二：dev - 开发辅助模块

## 1. 模块概述

KubeJS模板库、CraftTweaker模板库、数据包生成器。

## 2. 依赖关系

```
dev
├── core
└── ai (脚本生成)
```

## 3. 子包结构

```
com.aurora.dev/
├── kubejs/
│   ├── KubeJsService.java            # KubeJS服务
│   ├── KubeJsTemplate.java           # 模板模型
│   ├── KubeJsGenerator.java          # 代码生成器
│   ├── event/
│   │   ├── EventHandler.java         # 事件处理器
│   │   ├── EventTemplate.java        # 事件模板
│   │   └── EventType.java            # 事件类型
│   ├── recipe/
│   │   ├── RecipeBuilder.java        # 配方构建器
│   │   ├── RecipeTemplate.java       # 配方模板
│   │   └── RecipeType.java           # 配方类型
│   ├── item/
│   │   ├── ItemBuilder.java          # 物品构建器
│   │   ├── ItemTemplate.java         # 物品模板
│   │   └── ItemProperties.java       # 物品属性
│   └── block/
│       ├── BlockBuilder.java         # 方块构建器
│       └── BlockTemplate.java        # 方块模板
├── crafttweaker/
│   ├── CraftTweakerService.java      # CT服务
│   ├── ZenScriptGenerator.java       # ZenScript生成器
│   ├── ZenScriptTemplate.java        # ZenScript模板
│   ├── recipe/
│   │   ├── RecipeZen.java            # 配方ZenScript
│   │   └── RecipeZenBuilder.java     # 配方构建器
│   └── event/
│       ├── EventZen.java             # 事件ZenScript
│       └── EventZenBuilder.java      # 事件构建器
├── datapack/
│   ├── DatapackService.java          # 数据包服务
│   ├── DatapackBuilder.java          # 数据包构建器
│   ├── function/
│   │   ├── FunctionBuilder.java      # 函数构建器
│   │   └── McFunction.java           # mcfunction模型
│   ├── loot_table/
│   │   ├── LootTableBuilder.java     # 战利品表构建器
│   │   ├── Pool.java                 # 战利品池
│   │   └── LootEntry.java            # 战利品条目
│   ├── advancement/
│   │   ├── AdvancementBuilder.java   # 进度构建器
│   │   └── Advancement.java          # 进度模型
│   ├── tag/
│   │   ├── TagBuilder.java           # 标签构建器
│   │   └── Tag.java                  # 标签模型
│   └── predicate/
│       ├── PredicateBuilder.java     # 谓词构建器
│       └── Predicate.java            # 谓词模型
└── template/
    ├── TemplateManager.java          # 模板管理器
    ├── TemplateLibrary.java          # 模板库
    └── TemplateCategory.java         # 模板分类
```

## 4. 核心类设计

### 4.1 KubeJS模板

```java
public class KubeJsService {
    private final TemplateManager templateManager;
    
    public List<KubeJsTemplate> getTemplates(KubeJsCategory category) {
        return templateManager.getByCategory(category);
    }
    
    public String generate(KubeJsTemplate template, Map<String, Object> params) {
        return template.render(params);
    }
    
    public Path export(String code, String fileName, Path targetDir) {
        // 导出到kubejs目录
    }
}

public class KubeJsTemplate {
    private String id;
    private String name;
    private String description;
    private KubeJsCategory category;
    private String template;
    private List<TemplateParameter> parameters;
    private List<String> requiredMods;
    
    public String render(Map<String, Object> params) {
        String result = template;
        for (TemplateParameter param : parameters) {
            Object value = params.getOrDefault(param.getName(), param.getDefaultValue());
            result = result.replace("${" + param.getName() + "}", String.valueOf(value));
        }
        return result;
    }
    
    public enum KubeJsCategory {
        EVENT, RECIPE, ITEM, BLOCK, FLUID, ENTITY, WORLD, OTHER
    }
}

public class TemplateParameter {
    private String name;
    private String label;
    private String description;
    private ParameterType type;
    private Object defaultValue;
    private List<String> options;  // 用于SELECT类型
    
    public enum ParameterType {
        STRING, NUMBER, BOOLEAN, ITEM_ID, BLOCK_ID, FLUID_ID, SELECT
    }
}
```

### 4.2 Event Templates

```java
public class EventTemplate extends KubeJsTemplate {
    private EventType eventType;
    private String eventClass;
    
    public enum EventType {
        BLOCK_BREAK, BLOCK_PLACE, ITEM_CRAFTED, ITEM_SMELTED,
        ENTITY_DEATH, PLAYER_JOIN, PLAYER_QUIT, PLAYER_CHAT,
        SERVER_LOAD, SERVER_TICK, WORLD_LOAD, WORLD_TICK
    }
}

public class EventHandler {
    public String generateEventScript(EventTemplate template, 
                                       EventConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("// ").append(template.getName()).append("\n");
        sb.append("ServerEvents.").append(template.getEventType()).append("(event => {\n");
        sb.append(config.getLogic());
        sb.append("\n});");
        return sb.toString();
    }
}
```

### 4.3 Recipe Templates

```java
public class RecipeTemplate extends KubeJsTemplate {
    private RecipeType recipeType;
    
    public enum RecipeType {
        SHAPED, SHAPELESS, SMELTING, BLASTING, SMOKING,
        CAMPFIRE_COOKING, STONECUTTING, SMITHING, BREWING
    }
}

public class RecipeBuilder {
    private RecipeType type;
    private String id;
    private Item output;
    private List<Item> inputs;
    private String pattern;
    private Map<Character, Item> key;
    private float experience;
    private int cookingTime;
    
    public String buildKubeJs() {
        StringBuilder sb = new StringBuilder();
        sb.append("ServerEvents.recipes(event => {\n");
        
        switch (type) {
            case SHAPED -> buildShaped(sb);
            case SHAPELESS -> buildShapeless(sb);
            case SMELTING -> buildSmelting(sb);
            // ... 其他类型
        }
        
        sb.append("})");
        return sb.toString();
    }
    
    private void buildShaped(StringBuilder sb) {
        sb.append("  event.shaped('").append(output).append("', [\n");
        // 构建配方
    }
}

public class RecipeZenBuilder {
    public String buildZenScript() {
        // 生成CraftTweaker ZenScript
    }
}
```

### 4.4 Item/Block Builders

```java
public class ItemBuilder {
    private String id;
    private String displayName;
    private int maxStackSize = 64;
    private int maxDamage;
    private String rarity = "common";
    private List<String> tooltip;
    private FoodProperties food;
    private boolean glow;
    private String group;
    
    public String buildKubeJs() {
        StringBuilder sb = new StringBuilder();
        sb.append("StartupEvents.registry('item', event => {\n");
        sb.append("  event.create('").append(id).append("')\n");
        
        if (displayName != null) {
            sb.append("    .displayName('").append(displayName).append("')\n");
        }
        if (maxStackSize != 64) {
            sb.append("    .maxStackSize(").append(maxStackSize).append(")\n");
        }
        if (maxDamage > 0) {
            sb.append("    .maxDamage(").append(maxDamage).append(")\n");
        }
        if (!"common".equals(rarity)) {
            sb.append("    .rarity('").append(rarity).append("')\n");
        }
        if (food != null) {
            sb.append("    .food(food => {\n");
            sb.append("      food.hunger(").append(food.getHunger()).append(")\n");
            sb.append("      food.saturation(").append(food.getSaturation()).append(")\n");
            sb.append("    })\n");
        }
        
        sb.append("})");
        return sb.toString();
    }
}

public class BlockBuilder {
    private String id;
    private String displayName;
    private String material = "stone";
    private float hardness = 1.0f;
    private float resistance = 1.0f;
    private String harvestTool;
    private int harvestLevel;
    private boolean hasTileEntity;
    
    public String buildKubeJs() {
        // 类似ItemBuilder
    }
}
```

### 4.5 CraftTweaker

```java
public class CraftTweakerService {
    public List<ZenScriptTemplate> getTemplates();
    public String generate(ZenScriptTemplate template, Map<String, Object> params);
}

public class ZenScriptGenerator {
    public String generateRecipe(RecipeConfig config) {
        StringBuilder sb = new StringBuilder();
        
        switch (config.getType()) {
            case SHAPED -> {
                sb.append("craftingTable.addShaped(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  [\n");
                sb.append(buildPatternGrid(config));
                sb.append("  ]\n");
                sb.append(");\n");
            }
            case SHAPELESS -> {
                sb.append("craftingTable.addShapeless(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  [").append(buildIngredientList(config)).append("]\n");
                sb.append(");\n");
            }
            case SMELTING -> {
                sb.append("furnace.addRecipe(\"").append(config.getId()).append("\", \n");
                sb.append("  <item:").append(config.getInput()).append(">, \n");
                sb.append("  <item:").append(config.getOutput()).append(">, \n");
                sb.append("  ").append(config.getXp()).append(", \n");
                sb.append("  ").append(config.getTime()).append("\n");
                sb.append(");\n");
            }
        }
        
        return sb.toString();
    }
}
```

### 4.6 Datapack Builders

```java
public class DatapackBuilder {
    private String name;
    private String description;
    private int packFormat;
    private Map<String, String> functions = new HashMap<>();
    private Map<String, JsonElement> lootTables = new HashMap<>();
    private Map<String, JsonElement> advancements = new HashMap<>();
    private Map<String, JsonElement> tags = new HashMap<>();
    
    public DatapackBuilder function(String name, String content) {
        functions.put(name, content);
        return this;
    }
    
    public DatapackBuilder lootTable(String name, LootTableBuilder builder) {
        lootTables.put(name, builder.build());
        return this;
    }
    
    public DatapackBuilder advancement(String name, AdvancementBuilder builder) {
        advancements.put(name, builder.build());
        return this;
    }
    
    public DatapackBuilder tag(String name, TagBuilder builder) {
        tags.put(name, builder.build());
        return this;
    }
    
    public Path build(Path outputDir) {
        // 生成完整的数据包结构
        Path packDir = outputDir.resolve(name);
        
        // pack.mcmeta
        writePackMcmeta(packDir);
        
        // functions
        for (Map.Entry<String, String> entry : functions.entrySet()) {
            Path funcPath = packDir.resolve("data").resolve(entry.getKey().replace(":", "/functions/")).resolve(entry.getKey().split(":")[1] + ".mcfunction");
            Files.writeString(funcPath, entry.getValue());
        }
        
        // loot_tables
        for (Map.Entry<String, JsonElement> entry : lootTables.entrySet()) {
            writeJson(packDir, "loot_tables", entry.getKey(), entry.getValue());
        }
        
        // advancements
        for (Map.Entry<String, JsonElement> entry : advancements.entrySet()) {
            writeJson(packDir, "advancements", entry.getKey(), entry.getValue());
        }
        
        // tags
        for (Map.Entry<String, JsonElement> entry : tags.entrySet()) {
            writeJson(packDir, "tags", entry.getKey(), entry.getValue());
        }
        
        return packDir;
    }
}

public class LootTableBuilder {
    private String type = "minecraft:generic";
    private List<Pool> pools = new ArrayList<>();
    
    public LootTableBuilder pool(Pool pool) {
        pools.add(pool);
        return this;
    }
    
    public JsonElement build() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        JsonArray poolsArray = new JsonArray();
        for (Pool pool : pools) {
            poolsArray.add(pool.toJson());
        }
        json.add("pools", poolsArray);
        return json;
    }
    
    public static class Pool {
        private int rolls = 1;
        private float bonusRolls;
        private List<LootEntry> entries = new ArrayList<>();
        private List<Condition> conditions = new ArrayList<>();
        
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("rolls", rolls);
            JsonArray entriesArray = new JsonArray();
            for (LootEntry entry : entries) {
                entriesArray.add(entry.toJson());
            }
            json.add("entries", entriesArray);
            return json;
        }
    }
    
    public static class LootEntry {
        private String type = "minecraft:item";
        private String name;
        
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);
            json.addProperty("name", name);
            return json;
        }
    }
}

public class AdvancementBuilder {
    private String display;
    private String parent;
    private List<Criterion> criteria = new ArrayList<>();
    private List<String> requirements;
    
    public AdvancementBuilder display(DisplayConfig config) {
        this.display = config.toJson().toString();
        return this;
    }
    
    public AdvancementBuilder criterion(String name, Criterion criterion) {
        criteria.add(criterion);
        return this;
    }
    
    public JsonElement build() {
        JsonObject json = new JsonObject();
        
        if (parent != null) {
            json.addProperty("parent", parent);
        }
        
        JsonObject displayObj = new JsonObject();
        displayObj.addProperty("icon", display);
        json.add("display", displayObj);
        
        JsonObject criteriaObj = new JsonObject();
        for (Criterion criterion : criteria) {
            criteriaObj.add(criterion.getName(), criterion.toJson());
        }
        json.add("criteria", criteriaObj);
        
        return json;
    }
}

public class TagBuilder {
    private boolean replace = false;
    private List<String> values = new ArrayList<>();
    
    public TagBuilder add(String value) {
        values.add(value);
        return this;
    }
    
    public TagBuilder addAll(List<String> values) {
        this.values.addAll(values);
        return this;
    }
    
    public JsonElement build() {
        JsonObject json = new JsonObject();
        json.addProperty("replace", replace);
        JsonArray valuesArray = new JsonArray();
        for (String value : values) {
            valuesArray.add(value);
        }
        json.add("values", valuesArray);
        return json;
    }
}
```

### 4.7 TemplateManager

```java
public class TemplateManager {
    private final Path templatesDir;
    private final Map<String, List<KubeJsTemplate>> templates = new HashMap<>();
    
    public void loadTemplates() {
        // 从文件加载模板
        // 支持JSON/YAML格式
    }
    
    public List<KubeJsTemplate> getByCategory(KubeJsCategory category) {
        return templates.getOrDefault(category.name(), Collections.emptyList());
    }
    
    public Optional<KubeJsTemplate> getById(String id) {
        return templates.values().stream()
            .flatMap(List::stream)
            .filter(t -> t.getId().equals(id))
            .findFirst();
    }
    
    public void addTemplate(KubeJsTemplate template) {
        templates.computeIfAbsent(template.getCategory().name(), k -> new ArrayList<>())
            .add(template);
    }
    
    public void saveTemplate(KubeJsTemplate template) {
        // 保存模板到文件
    }
}

public class TemplateLibrary {
    // 内置模板库
    public static List<KubeJsTemplate> getBuiltInTemplates() {
        return List.of(
            // 事件模板
            createEventTemplate("block_break", "Block Break Event", 
                "BlockEvents.broken('${block}', event => {\n  // Your code here\n});"),
            
            // 配方模板
            createRecipeTemplate("shaped_basic", "Basic Shaped Recipe", 
                "event.shaped('${output}', [\n  '${pattern}'\n], {\n  ${keys}\n});"),
            
            // 物品模板
            createItemTemplate("simple_item", "Simple Item",
                "event.create('${id}').displayName('${name}');"),
            
            // ...
        );
    }
}
```

## 5. 关键流程

### 5.1 配方生成流程

```
用户选择配方类型
    ↓
填写配方参数:
├── 输出物品
├── 输入材料
├── 配方图案(有形状)
└── 其他属性
    ↓
选择目标格式:
├── KubeJS
└── CraftTweaker
    ↓
生成代码
    ↓
预览和编辑
    ↓
导出到文件
```

### 5.2 数据包生成流程

```
创建数据包项目
    ↓
添加组件:
├── 函数文件
├── 战利品表
├── 进度
├── 标签
└── 谓词
    ↓
配置数据包信息
    ↓
构建数据包
    ↓
导出/打包
```

## 6. 配置文件

```json
{
  "dev": {
    "kubejs": {
      "outputDir": "./kubejs",
      "templatesDir": "./templates/kubejs"
    },
    "crafttweaker": {
      "outputDir": "./scripts",
      "templatesDir": "./templates/crafttweaker"
    },
    "datapack": {
      "outputDir": "./datapacks",
      "defaultPackFormat": 26
    }
  }
}
```

## 7. 测试要点

- 模板渲染正确性
- KubeJS代码生成
- CraftTweaker代码生成
- 数据包结构完整性
- JSON格式正确性
- 文件导出功能