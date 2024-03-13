package com.proferoman;

import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

public class App extends GameApplication {
	private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setTitle("Basic Game App");
        settings.setVersion("0.1");
    }

	@Override
	protected void initGame() {
		player = FXGL.entityBuilder()
			.type(EntityType.PLAYER)
			.at(300, 300)
			.viewWithBBox("actor.png")
			.with(new CollidableComponent(true))
			.buildAndAttach();

		FXGL.entityBuilder()
			.type(EntityType.COIN)
			.at(500, 200)
			.viewWithBBox(new Circle(15, 15, 15, Color.YELLOW))
			.with(new CollidableComponent(true))
			.buildAndAttach();
	}

	@Override
	protected void initPhysics() {
		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.COIN) {

			// order of types is the same as passed into the constructor
			@Override
			protected void onCollisionBegin(Entity player, Entity coin) {
				coin.setPosition(new Vec2(Math.random() * 500, Math.random() * 500));
				FXGL.inc("score", 1);
				int score = FXGL.getWorldProperties().intProperty("score").intValue();
				FXGL.set("scoreText", "Score: " + String.valueOf(score));
				//coin.removeFromWorld();
			}
		});

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {

			// order of types is the same as passed into the constructor
			@Override
			protected void onCollisionBegin(Entity player, Entity wall) {
				int threshold = 10;

				FXGL.inc("score", -1);
				int score = FXGL.getWorldProperties().intProperty("score").intValue();
				FXGL.set("scoreText", "Score: " + String.valueOf(score));

				var px1 = player.getX();
				var px2 = player.getRightX();
				var py1 = player.getY();
				var py2 = player.getBottomY();

				var wx1 = wall.getX();
				var wx2 = wall.getRightX();
				var wy1 = wall.getY();
				var wy2 = wall.getBottomY();

				System.out.println("PLAYER: x=(" + px1 + ", " + px2 + ") | y=(" + py1 + ", " + py2 + ")");
				System.out.println("WALL:   x=(" + wx1 + ", " + wx2 + ") | y=(" + wy1 + ", " + wy2 + ")");

				if ((px1 >= wx1 || px1 <= wx1) && (py2 >= wy1 && py2 <= wy1 + threshold)) {
					// Up and (right or left)
					player.setY(py1 - threshold);
				} else if ((px1 >= wx1 || px1 <= wx1) && (py1 <= wy2 && py1 >= wy2 - threshold)) {
					// Down and (right or left)
					player.setY(py1 + threshold);
				} else if ((py1 <= wy1 || py1 >= wy1) && (px1 <= wx2 && px1 >= wx2 - threshold)) {
					// Right and (up or down)
					player.setX(px1 + threshold);
				} else if ((py1 <= wy1 || py1 >= wy1) && (px2 >= wx1 && px2 <= wx1 + threshold)) {
					// Left and (up or down)
					player.setX(px1 - threshold);
				}
			}
		});
	}

	@Override
	protected void initInput() {
		FXGL.onKey(KeyCode.D, () -> {
				player.translateX(5); // move right 5 
			});
		FXGL.onKey(KeyCode.A, () -> {
				player.translateX(-5); // move left 5 pixels
			});
		FXGL.onKey(KeyCode.W, () -> {
				player.translateY(-5); // move up 5 pixels
			});
		FXGL.onKey(KeyCode.S, () -> {
				player.translateY(5); // move down 5 pixels
			});
			
		FXGL.onKeyDown(KeyCode.F, () -> {
			FXGL.play("drop.wav");
		}); 
	}

	@Override
	protected void initUI() {
		Text textScore = new Text();
		textScore.setTranslateX(5); // x = 5
		textScore.setTranslateY(10); // y = 10

		textScore.textProperty().bind(FXGL.getWorldProperties().stringProperty("scoreText"));

		FXGL.getGameScene().addUINode(textScore); // add to the scene graph

		//var b1 = FXGL.getAssetLoader().loadTexture("brick.png");
		//b1.setTranslateX(50);
		//b1.setTranslateY(450);
		var b1 = FXGL.entityBuilder()
			.type(EntityType.WALL)
			.at(50, 450)
			.viewWithBBox("brick.png")
			.with(new CollidableComponent(true))
			.buildAndAttach();
		var x = b1.getWidth();
		var y = b1.getHeight();

		//var b2 = FXGL.getAssetLoader().loadTexture("brick.png");
		//b2.setTranslateX(50 + x);
		//b2.setTranslateY(450);
		var b2 = FXGL.entityBuilder()
			.type(EntityType.WALL)
			.at(50 + x, 450)
			.viewWithBBox("brick.png")
			.with(new CollidableComponent(true))
			.buildAndAttach();

		//FXGL.getGameScene().addUINode(b1);
		//FXGL.getGameScene().addUINode(b2);
	}

	@Override
	protected void initGameVars(Map<String, Object> vars) {
		vars.put("scoreText", "Score: 0");
		vars.put("score", 0);
	}

    public static void main(String[] args) {
        launch(args);
    }
}
