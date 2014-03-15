package KAT;

import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorAdjustBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

public abstract class GameMenu {

	protected double width, height;
	
	protected Rectangle clip;
	protected ImageView backingImgV;
	protected Group menuNode;
	
	protected GameMenu() {

		width = Game.getWidth() * 0.8;
		height = Game.getHeight() * 0.8;
		
		
		clip = RectangleBuilder.create()
				.width(width)
				.height(height)
				.arcHeight(height * 0.05)
				.arcWidth(width * 0.05)
				.build();
		
//		ColorAdjust colorAdj = ColorAdjustBuilder.create()
//				.brightness(-0.4)
//				.contrast(-0.2)
//				//.hue(-0.3)
//				.build();
		
		backingImgV = ImageViewBuilder.create()
				.image(new Image("Images/RackCover.jpg"))
				.preserveRatio(false)
				.fitHeight(height)
				.fitWidth(width)
//				.effect(colorAdj)
				.build();
		
		Rectangle temp = RectangleBuilder.create()
				.width(width)
				.height(height)
				.fill(Color.BLACK)
				.build();
		
		menuNode = GroupBuilder.create()
				.clip(clip)
				.children(temp, backingImgV)
				.layoutX(Game.getWidth()/2 - width/2)
				.layoutY(height * 0.1/0.8)
				.build();
	}

	public Group getNode() { return menuNode; }
	public abstract void remove();
	
	
}
