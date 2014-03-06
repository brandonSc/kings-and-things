package KAT;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.GlowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

public class GameButton {

	private static Image defaultImage;
	
	private Group buttonNode;
	private Text text;
	private String textString;
	private ImageView imgV;
	private boolean active;
	private Rectangle clip;
	private Rectangle cover;
	private Rectangle border;
	private double width, height;
	
	/*
	 * Constructors
	 */
	public GameButton () {
		width = 100;
		height = 50;
		textString = " ";
		active = false;
		setupGUI();
	}
	public GameButton(double w, double h, String t) {
		width = w;
		height = h;
		textString = t;
		active = false;
		setupGUI();
	}
	
	private void setupGUI() {
		
		final Glow glow = GlowBuilder.create().build();
		
		if (defaultImage == null) 
			defaultImage = new Image("Images/ButtonBacking.jpg");
		
		text = TextBuilder.create()
				.text(textString)
				.mouseTransparent(true)
				.font(Font.font("Verdana", FontPosture.ITALIC, 20))
				.fill(Color.BLACK)
				.visible(true)
				.build();
		
		clip = RectangleBuilder.create()
				.width(width)
				.height(height)
				.arcHeight(20)
				.arcWidth(20)
				.build();
		
		cover = RectangleBuilder.create()
				.width(width)
				.height(height)
				.fill(Color.DARKSLATEGRAY)
				.opacity(0.5)
				.visible(false)
				.disable(true)
				.build();
		
		border = RectangleBuilder.create()
				.width(width)
				.height(height)
				.arcHeight(20)
				.arcWidth(20)
				.stroke(Color.BLACK)
				.strokeWidth(3)
				.fill(Color.TRANSPARENT)
				.mouseTransparent(true)
				.effect(new GaussianBlur(2))
				.build();
				
		imgV = ImageViewBuilder.create()
				.image(defaultImage)
				.preserveRatio(false)
				.fitHeight(height)
				.fitWidth(width)
				.onMouseEntered(new EventHandler(){
					@Override
					public void handle(Event event) {
						imgV.setEffect(glow);
					}
				})
				.onMouseExited(new EventHandler(){
					@Override
					public void handle(Event event) {
						imgV.setEffect(null);
					}
				}) 
				.build();
		
		buttonNode = GroupBuilder.create()
				.clip(clip)
				.build();
		
		text.relocate(clip.getWidth()/2 - text.getLayoutBounds().getWidth()/2, clip.getHeight()/2 - text.getLayoutBounds().getHeight()/2);
		buttonNode.getChildren().add(imgV);
		buttonNode.getChildren().add(border);
		buttonNode.getChildren().add(text);
		buttonNode.getChildren().add(cover);
				
	}
	
	/*
	 * Gets and Sets
	 */
	public boolean getActive() { return active; }
	public Group getNode() { return buttonNode; }
	public double getWidth() { return width; }
	public double getHeight() { return height; }
	
	public void activate() {
		active = true;
		cover.setVisible(false);
		cover.setDisable(true);
	}
	public void deactivate() {
		active = false;
		cover.setVisible(true);
		cover.setDisable(false);
	}
	public void setAction(EventHandler eh) {
		imgV.setOnMouseClicked((javafx.event.EventHandler<? super MouseEvent>) eh);
	}
	public void setText(String s) {
		textString = s;
		text = TextBuilder.create()
				.text(textString)
				.mouseTransparent(true)
				.build();
	}
	
}
