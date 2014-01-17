package KAT;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
 * Terrain class
 */
public class Terrain {
        /* 
         * Enum to list the different possible types of terrain tiles
         */
        public static enum TERRAINS {
                JUNGLE, FROZENWASTE, FOREST, PLAINS, SWAMP, MOUNTAIN, DESERT, SEA
        }
        // TODO: clean up the following Images maybe?
        private static Image baseTileImageDesert, baseTileImageForest, baseTileImageFrozenWaste, baseTileImageJungle, baseTileImageMountain, baseTileImagePlains, baseTileImageSea, baseTileImageSwamp, baseTileImageUpsideDown;
        private static String imageSet = "01"; // Was trying different images, this will be removed in future.
        private static double sideLength;
        
        private String type;
        private boolean occupied; //True if another player owns it, otherwise false
        private Image tileImage;
        private ArrayList<Piece> contents; // List of peices on this hex
        private int[] coords;
        private Group hexNode;
        private Hex hexClip;
        private ImageView imageView;
        
        /*
         * Constructors:
         */
        public Terrain() {
                type = "";
                occupied = false;
                coords = new int[]{0, 0, 0};
                contents = new ArrayList<Piece>();
                hexNode = new Group();
            	hexClip = new Hex(sideLength, true);
            	hexNode.setClip(hexClip.getHex());
        }
        
        public Terrain(Group boardNode, String t, int[] xyz) {
        	type = t;
        	occupied = false;
        	coords = xyz;
        	tileImage = baseTileImageDesert;
        	
        	
        	contents = new ArrayList<Piece>();
        	hexNode = new Group();
        	hexClip = new Hex(sideLength * Math.sqrt(3), true);
        	
        	// Move each hex to the correct position
        	hexNode.setClip(hexClip.getHex());
                hexNode.relocate(1.5 * hexClip.getSideLength() * (xyz[0] + 3), (6 - xyz[1] + xyz[2]) * sideLength * Math.sqrt(3)/2 + (Math.sqrt(3)*sideLength)/6);
        	
        	imageView = new ImageView();
        	setImageViews();
        	boardNode.getChildren().add(hexNode);
        }

        /* 
         * Get/Set methods
         */
        public boolean isOccupied() { return occupied; }
        public String getType() { return type; }

        public void setOccupied(boolean b) { occupied = b;}
        public void setType(String s) { type = s; }
        
        public void setImage(Image img) { this.tileImage = img; }
        public Image getImage() { return tileImage; }
        
        /*
         * runs when board is constructed. Loads one image of each tile type
         */
        public static void setBaseImages() {
        	baseTileImageDesert = new Image("Images/Hex_desert_" + imageSet + ".png");
        	baseTileImageForest = new Image("Images/Hex_forest_" + imageSet + ".png");
        	baseTileImageFrozenWaste = new Image("Images/Hex_frozenwaste_" + imageSet + ".png");
        	baseTileImageJungle = new Image("Images/Hex_jungle_" + imageSet + ".png");
        	baseTileImageMountain = new Image("Images/Hex_mountains_" + imageSet + ".png");
        	baseTileImagePlains = new Image("Images/Hex_plains_" + imageSet + ".png");
        	baseTileImageSea = new Image("Images/Hex_sea_" + imageSet + ".png");
        	baseTileImageSwamp = new Image("Images/Hex_swamp_" + imageSet + ".png");
        	baseTileImageUpsideDown = new Image("Images/Hex_upsidedown_" + imageSet + ".png");
        }
        
        public static void setSideLength(double sl) { sideLength = sl; }
        
        
        private void setImageViews() {
        	
        	imageView.setImage(tileImage);
        	imageView.setFitHeight(hexClip.getHeightNeeded());
        	imageView.setPreserveRatio(true);
        	// TODO add imageViews for each Peice on hex
        	hexNode.getChildren().add(imageView);
        }
        
}
