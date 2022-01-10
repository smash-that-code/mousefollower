package follower.concept;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import follower.concept.data.CircleEntity;
import follower.concept.data.SquareEntity;
import follower.concept.data.InputState;
import follower.concept.geometry.Direction;
import follower.concept.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class MouseFollower extends ApplicationAdapter {
	//GDX specific
	SpriteBatch batch;
	Texture playerTexture, npcTexture;
	Sprite playerSprite, npcSprite;

	public static class GameState {
		public int screenMaxWidth;
		public int screenMaxHeight;

		public GameState(int screenMaxWidth, int screenMaxHeight) {
			this.screenMaxWidth = screenMaxWidth;
			this.screenMaxHeight = screenMaxHeight;
		}

		public SquareEntity player =
				new SquareEntity(150, 150, 100, 0, 550);

		public List<CircleEntity> npcList = new ArrayList<>();
	}

	//Program state specific
	GameState state;
	InputState inputState;

	@Override
	public void create () {
		state = new GameState(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		inputState = new InputState();

		batch = new SpriteBatch();
		playerTexture = new Texture("square.png");
		playerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		playerSprite = new Sprite(playerTexture);
		npcTexture = new Texture("circle.png");
		npcTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		npcSprite = new Sprite(npcTexture);

		state.npcList.add(new CircleEntity(300,300, 20, 0, 10));


		Gdx.input.setInputProcessor(new InputAdapter() {

			@Override
			public boolean keyDown (int keyCode) {
				if (keyCode == Input.Keys.ESCAPE) {
					inputState.keyboardKeyState.put(Keyboard.ESCAPE, Boolean.TRUE);
				}
				else if (keyCode == Input.Keys.SPACE) {
					inputState.keyboardKeyState.put(Keyboard.SPACE, Boolean.TRUE);
				}
				return true;
			}

			@Override
			public boolean keyUp(int keyCode) {
				if (keyCode == Input.Keys.SPACE) {
					inputState.keyboardKeyState.put(Keyboard.SPACE, Boolean.FALSE);
				}
				return true;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				inputState.isClicked = true;
				inputState.mouseX = screenX;
				inputState.mouseY = state.screenMaxHeight - screenY;
				Gdx.app.log("", inputState.mouseX + ", " + inputState.mouseY);
				return true;
			}
		});
	}

	public static void handleKeyboardInput(Input input, InputState inputState) {
		inputState.keyboardKeyState.put(Keyboard.W, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.A, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.S, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.D, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.UP, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.DOWN, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.LEFT, Boolean.FALSE);
		inputState.keyboardKeyState.put(Keyboard.RIGHT, Boolean.FALSE);

		if (input.isKeyPressed(Input.Keys.W)) {
			inputState.keyboardKeyState.put(Keyboard.UP, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.S)) {
			inputState.keyboardKeyState.put(Keyboard.DOWN, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.A)) {
			inputState.keyboardKeyState.put(Keyboard.LEFT, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.D)) {
			inputState.keyboardKeyState.put(Keyboard.RIGHT, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.LEFT)) {
			inputState.keyboardKeyState.put(Keyboard.LEFT, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.RIGHT)) {
			inputState.keyboardKeyState.put(Keyboard.RIGHT, Boolean.TRUE);
		}

		if (input.isKeyPressed(Input.Keys.UP)) {
			inputState.keyboardKeyState.put(Keyboard.UP, Boolean.TRUE);
		}
		else if (input.isKeyPressed(Input.Keys.DOWN)) {
			inputState.keyboardKeyState.put(Keyboard.DOWN, Boolean.TRUE);
		}
	}

	public static void checkExit(InputState inputState) {
		if (inputState.keyboardKeyState.get(Keyboard.ESCAPE) == Boolean.TRUE) {
			Gdx.app.exit();
			System.exit(0);
		}
	}

	public static void applyPlayerInput(InputState inputState, GameState state, float delta) {
		if (inputState.keyboardKeyState.get(Keyboard.LEFT) != Boolean.TRUE
			&& inputState.keyboardKeyState.get(Keyboard.RIGHT) != Boolean.TRUE
			&& inputState.keyboardKeyState.get(Keyboard.UP) != Boolean.TRUE
			&& inputState.keyboardKeyState.get(Keyboard.DOWN) != Boolean.TRUE) {
			//no input
			return;
		}

		float x = 0;
		if (inputState.keyboardKeyState.get(Keyboard.LEFT) == Boolean.TRUE) {
			x = -1;
		}
		else if (inputState.keyboardKeyState.get(Keyboard.RIGHT) == Boolean.TRUE) {
			x = 1;
		}

		float y = 0;
		if (inputState.keyboardKeyState.get(Keyboard.UP) == Boolean.TRUE) {
			y = 1;
		}
		else if (inputState.keyboardKeyState.get(Keyboard.DOWN) == Boolean.TRUE) {
			y = -1;
		}

		Direction direction = Direction.getDirection(x, y);
		float rotation = direction.degreeAngle;


		float directionX = (float) Math.cos(Math.PI / 180 * rotation);
		float directionY = (float) Math.sin(Math.PI / 180 * rotation);

		float step = state.player.speed * delta;
		state.player.x += directionX * step;
		state.player.y += directionY * step;
		state.player.rotation = rotation;
		state.player.direction = direction;
	}

	//do not allow player move beyond screen
	public static void calculatePlayerCollisions(GameState state) {
		SquareEntity player = state.player;
		float halfSide = player.side/2;

		//against borders
		if (player.x > state.screenMaxWidth - halfSide) {
			player.x = state.screenMaxWidth - halfSide;
		} else if (player.x < halfSide) {
			player.x = halfSide;
		}

		if (player.y > state.screenMaxHeight - halfSide) {
			player.y = state.screenMaxHeight - halfSide;
		} else if (player.y < halfSide) {
			player.y = halfSide;
		}
	}

	public final int MAX_UPDATE_ITERATIONS = 3;
	public final float FIXED_TIMESTAMP = 1/60f;
	private float internalTimeTracker = 0;

	@Override
	public void render () {
		//input handling
		checkExit(inputState);
		handleKeyboardInput(Gdx.input, inputState);

		//fixed-timestamp logic handling
		float delta = Gdx.graphics.getDeltaTime();
		internalTimeTracker += delta;
		int iterations = 0;

		while(internalTimeTracker > FIXED_TIMESTAMP && iterations < MAX_UPDATE_ITERATIONS) {
			//apply input
			applyPlayerInput(inputState, state, FIXED_TIMESTAMP);

			//collision detection
			calculatePlayerCollisions(state);

			//apply npc movement changes
			if (inputState.isClicked) {
				CircleEntity npc = state.npcList.get(0);

				int desiredX = inputState.mouseX;
				int desiredY = inputState.mouseY;
				if (Math.abs(desiredX - npc.x) <= npc.speed) {
					npc.x = desiredX;
				}
				else if (npc.x != desiredX) {
					npc.x += (int) Math.signum(desiredX - npc.x)*npc.speed;
				}

				if (Math.abs(desiredY - npc.y) <= npc.speed) {
					npc.y = desiredY;
				}
				else if (npc.y != desiredY) {
					npc.y += (int) Math.signum(desiredY - npc.y)*npc.speed;
				}
			}

			//time tracking logic
			internalTimeTracker -= FIXED_TIMESTAMP;
			iterations++;
		}

		//render
		Gdx.gl.glClearColor(0.95f, 0.95f, 0.95f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		//this is where we actually draw stuff
		batch.begin();

		//npcs
		for (CircleEntity npc: state.npcList) {
			npcSprite.setBounds(npc.x-npc.radius, npc.y-npc.radius, npc.radius*2 , npc.radius*2);
			npcSprite.setOriginCenter();
			npcSprite.setRotation(npc.rotation);
			npcSprite.draw(batch);
		}

		//player
		SquareEntity player = state.player;
		playerSprite.setBounds(player.x-player.side/2, player.y-player.side/2, player.side , player.side);
		playerSprite.setOriginCenter();
		playerSprite.setRotation(player.rotation);
		playerSprite.draw(batch);

		batch.end();
	}

	//it should be called when we exit our program
	@Override
	public void dispose () {
		batch.dispose();
		playerTexture.dispose();
	}

}