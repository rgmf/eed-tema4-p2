package com.proferoman;

import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;

import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class App extends GameApplication {
	private static int W = 800;
	private static int H = 800;

	private Game game;
	private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(W);
        settings.setHeight(H);
        settings.setTitle("EED: UML: Game App");
        settings.setVersion("0.0.1");
    }

	@Override
	protected void initGame() {
		game = Game.newInstance(W, H);
		Player gamePlayer = game.getPlayer();
		Canyon canyon = game.getCanyon();

		player = FXGL.entityBuilder()
			.type(ActorType.PLAYER)
			.at(gamePlayer.getX(), gamePlayer.getY())
			.viewWithBBox(FXGL.texture(gamePlayer.getTexture(), gamePlayer.getWidth(), gamePlayer.getHeight()))
			.with(new CollidableComponent(true))
			.buildAndAttach();

		FXGL.entityBuilder()
			.type(ActorType.CANYON)
			.viewWithBBox(new Circle(canyon.getCenterX(), canyon.getCenterY(), canyon.getRadius(), Color.YELLOW))
			.with(new CollidableComponent(true))
			.buildAndAttach();

		ObstacleFactory.buildForGame(game, 10);
		for (Obstacle o : game.getObstacles()) {
			FXGL.entityBuilder()
				.type(ActorType.WALL)
				.at(o.getX(), o.getY())
				.viewWithBBox(FXGL.texture(o.getTexture(), o.getWidth(), o.getHeight()))
				.with(new CollidableComponent(true))
				.buildAndAttach();
		}

		FXGL.run(() -> {
			Enemy enemy = new Enemy();
			FXGL.entityBuilder()
                .type(ActorType.ENEMY)
				.at(W / 2 - 30 / 2, H / 2 - 20 / 2)
                .viewWithBBox(FXGL.texture("sprite_bullet.png", 30, 20))
                .with((new AutoRotationComponent()).withSmoothing())
                .with(enemy)
                .collidable()
                .buildAndAttach();
		}, Duration.seconds(1));

		FXGL.run(() -> {
			FXGL.inc("score", 1);
		}, Duration.seconds(5));
	}

	@Override
	protected void initPhysics() {
		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(ActorType.PLAYER, ActorType.COIN) {

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

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(ActorType.PLAYER, ActorType.WALL) {

			// order of types is the same as passed into the constructor
			@Override
			protected void onCollisionBegin(Entity player, Entity wall) {
				int threshold = 10;
				
				var px1 = player.getX();
				var px2 = player.getRightX();
				var py1 = player.getY();
				var py2 = player.getBottomY();

				var wx1 = wall.getX();
				var wx2 = wall.getRightX();
				var wy1 = wall.getY();
				var wy2 = wall.getBottomY();

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

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(ActorType.ENEMY, ActorType.WALL) {

			// order of types is the same as passed into the constructor
			@Override
			protected void onCollisionBegin(Entity enemy, Entity wall) {
				enemy.removeFromWorld();
			}
		});

		FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(ActorType.ENEMY, ActorType.PLAYER) {

			// order of types is the same as passed into the constructor
			@Override
			protected void onCollisionBegin(Entity enemy, Entity player) {
				FXGL.inc("lives", -1);
				if (FXGL.geti("lives") <= 0) {
					FXGL.getDialogService().showMessageBox(
						"Game Over",
						() -> FXGL.getGameController().exit()
					);
				} else {
					player.setPosition(10, 10);
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
	}

	@Override
	protected void initUI() {
		Text scoreLabel = FXGL.getUIFactoryService().newText("Score", Color.BLACK, 22);
        Text scoreValue = FXGL.getUIFactoryService().newText("", Color.BLACK, 22);
        Text livesLabel = FXGL.getUIFactoryService().newText("Lives", Color.BLACK, 22);
        Text livesValue = FXGL.getUIFactoryService().newText("", Color.BLACK, 22);

        scoreLabel.setTranslateX(20);
        scoreLabel.setTranslateY(20);

        scoreValue.setTranslateX(90);
        scoreValue.setTranslateY(20);

        livesLabel.setTranslateX(FXGL.getAppWidth() - 150);
        livesLabel.setTranslateY(20);

        livesValue.setTranslateX(FXGL.getAppWidth() - 80);
        livesValue.setTranslateY(20);

        scoreValue.textProperty().bind(FXGL.getWorldProperties().intProperty("score").asString());
        livesValue.textProperty().bind(FXGL.getWorldProperties().intProperty("lives").asString());

        FXGL.getGameScene().addUINodes(scoreLabel, scoreValue, livesLabel, livesValue);
	}

	@Override
	protected void initGameVars(Map<String, Object> vars) {
		vars.put("score", 0);
		vars.put("lives", 3);
	}

    public static void main(String[] args) {
        launch(args);
    }
}
