package org.aurora.launcher.ui.service;

public class RecipeTemplate {
    
    public enum Category {
        KUBEJS,
        CRAFTTWEAKER,
        DATAPACK
    }
    
    private String id;
    private String name;
    private String nameZh;
    private String code;
    private String description;
    private String descriptionZh;
    private Category category;
    private String recipeType;
    
    public RecipeTemplate() {}
    
    public RecipeTemplate(String id, String name, String nameZh, String code, 
                         String description, String descriptionZh, Category category) {
        this.id = id;
        this.name = name;
        this.nameZh = nameZh;
        this.code = code;
        this.description = description;
        this.descriptionZh = descriptionZh;
        this.category = category;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getNameZh() { return nameZh; }
    public void setNameZh(String nameZh) { this.nameZh = nameZh; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDescriptionZh() { return descriptionZh; }
    public void setDescriptionZh(String descriptionZh) { this.descriptionZh = descriptionZh; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public String getRecipeType() { return recipeType; }
    public void setRecipeType(String recipeType) { this.recipeType = recipeType; }
    
    public static RecipeTemplate shaped(String name, String nameZh, String code, String desc, String descZh) {
        RecipeTemplate t = new RecipeTemplate();
        t.setId("shaped_" + name.toLowerCase().replace(" ", "_"));
        t.setName(name);
        t.setNameZh(nameZh);
        t.setCode(code);
        t.setDescription(desc);
        t.setDescriptionZh(descZh);
        t.setCategory(Category.KUBEJS);
        t.setRecipeType("shaped");
        return t;
    }
    
    public static RecipeTemplate shapeless(String name, String nameZh, String code, String desc, String descZh) {
        RecipeTemplate t = new RecipeTemplate();
        t.setId("shapeless_" + name.toLowerCase().replace(" ", "_"));
        t.setName(name);
        t.setNameZh(nameZh);
        t.setCode(code);
        t.setDescription(desc);
        t.setDescriptionZh(descZh);
        t.setCategory(Category.KUBEJS);
        t.setRecipeType("shapeless");
        return t;
    }
}
