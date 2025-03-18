package gameObjects;

import java.util.Random;

import gameGUI.GameImage;
import gameMath.Vector;

import java.io.File;


public class CharComponent {
    private ComponentType type;
    private Variation variation;

    public static class Variation {
        private String name, directory;

        public Variation(String name){
            this(name, null);
        }
       
        public Variation(String name, String directory){
            this.name = name;
            this.directory = directory;
        }

        public String getName(){
            return name;
        }

        public String getDirectory(){
            return directory;
        }

        public boolean equals(Variation other){
            return name.equals(other.getName());
        }
    }

    public enum ComponentType{
        SKINCOLOR,
        SHIRT,
        SHOES,
        NAME,
        MURDERWEAPON;

        private final String[] NAME_VARIATIONS = {"Kai", "Adrian", "Luca", "Luke" ,"Raphael",
            "Tommy", "Jackson", "Nick", "Nathan", "Jack", "Tyler", "David", "Patrick", "Ivan",
            "Nadden", "Jordan", "Kobe", "Sebastien", "Mike", "Kevin", "Larry", "Daniel", "Anthony",
            "Jayden", "Seth", "Tariq", "Tyrone"
        };


        private String getDirectoryFolderName(){
            switch(this){
                case SKINCOLOR:
                    return "assets/art/heads";
                case SHIRT:
                    return "assets/art/shirts";
                case SHOES:
                    return "assets/art/shoes";
                case MURDERWEAPON:
                    return "assets/art/weaponPosters";
                default:
                    return null;
             }
        }

        /*
         * @return Gets the files of each possible variation for a given ComponentType (if applicable).
         */
        private File[] getVariations(String variationType){
            if(this == ComponentType.NAME){ return null; }

            try{
                File folder = this == ComponentType.MURDERWEAPON ?
                new File(getDirectoryFolderName()): new File(getDirectoryFolderName() + "/" + variationType);
                File[] children = folder.listFiles();
                //Subtract 1 from children.length to exclude the special folder
                File[] variations = new File[children.length];
                int j = 0;
                for(int i = 0; i < children.length; i++){
                    if(children[i].isDirectory()){ continue; }
                    variations[j] = children[i];
                    j++;
                }
                return variations;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Variation pickVariation(String variationType){
            Random numGen = new Random();
            switch(this){
                case NAME:
                    return new Variation(NAME_VARIATIONS[numGen.nextInt(NAME_VARIATIONS.length)]);
                default:
                    File[] variations = getVariations(variationType);
                    File choice = variations[numGen.nextInt(variations.length)];
                    return new Variation(choice.getName().replace(".png", ""), choice.getPath());
            }
        }
    }

    public CharComponent(ComponentType type, String charClass) {
        this(type, type.pickVariation(getVariationType(charClass)));
    }

    public CharComponent(ComponentType type, String charClass, String variationName){
        if(type == ComponentType.NAME){ 
            this.type = type;
            this.variation = new Variation(variationName);
        } else {
            String folder = type.getDirectoryFolderName();
            folder = type == ComponentType.MURDERWEAPON ? folder : folder + "/" + getVariationType(charClass);
            this.type = type;
            this.variation = new Variation(variationName, folder + "/" + variationName + ".png");
        }
    }

    public CharComponent(ComponentType type, Variation variation) {
        this.type = type;
        this.variation = variation;
       
    }

    public GameImage createImage(Vector pos){
        if(type == ComponentType.NAME){ return null; }
        return new GameImage(variation.getDirectory(), pos);
    }
    
    public ComponentType getType(){
        return type;
    }

    public Variation getVariation(){
        return variation;
    }

    public boolean equals(CharComponent other){
        return type == other.getType() && variation.equals(other.getVariation());
    }

    private static String getVariationType(String charClass){
        if(charClass.equals("Doctor") || charClass.equals("Detective")
        || charClass.equals("Victim")){
            return "special";
        } else  {
            return charClass.toLowerCase();
        }
    }

}
