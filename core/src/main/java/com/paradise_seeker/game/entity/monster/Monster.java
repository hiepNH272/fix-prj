package com.paradise_seeker.game.entity.monster;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.paradise_seeker.game.entity.Character;
import com.paradise_seeker.game.entity.Collidable;
import com.paradise_seeker.game.entity.player.Player;
import com.paradise_seeker.game.map.GameMap;


public abstract class Monster extends Character {

    public boolean isDead = false;
    public float spawnX;
    public float spawnY;

    public MonsterAnimationManagerImpl animationManager;
    public MonsterCollisionHandler collisionHandler;
    public HPBarMonsterRenderer renderer;
    public MonsterAI ai;
    public TextureRegion currentFrame;

    public float stateTime = 0f;
    public boolean facingRight = true;

    public boolean isTakingHit = false;
    public float takeHitTimer = 0f;
    public float takeHitDuration = 0.5f;

    public boolean isCleaving = false;
    public float cleaveTimer = 0f;
    public float cleaveDuration = 1.2f;
    private boolean cleaveDamageApplied = false; // Đảm bảo chỉ trừ máu 1 lần/mỗi đòn chém

    public Vector2 lastPosition = new Vector2();
    public boolean isMoving = false;

    public Monster(Rectangle bounds, float hp, float mp, float maxHp, float maxMp, float atk, float speed, float x, float y) {
        super(bounds, hp, mp, maxHp, maxMp, atk, speed, x, y);
        this.spawnX = x;
        this.spawnY = y;

        // Initialize managers
        this.renderer = new HPBarMonsterRenderer();
        this.animationManager = new MonsterAnimationManagerImpl(this);
        this.collisionHandler = new MonsterCollisionHandler(this);
        this.ai = new MonsterAI(this);

        // Set initial position
        this.lastPosition.set(x, y);
        this.bounds = new Rectangle(x, y, bounds.width, bounds.height);

        // Load animations (to be implemented by subclasses)
        loadAnimations();
    }

    @Override
    public void act(float deltaTime, GameMap map) {
    	Player player = map.getPlayer();
		this.act(deltaTime, player, map);
	}

    public void act(float deltaTime, Player player, GameMap map) {

        if (player == null || player.isDead) return;

        // Update AI first
        ai.update(deltaTime, player, map);

        // Track movement
        isMoving = lastPosition.dst(bounds.x, bounds.y) > 0.0001f;
        lastPosition.set(bounds.x, bounds.y);

        // Update animation state
        animationManager.update(deltaTime, isMoving, isDead, false, player.getBounds().x);
    }

	 public void cleave(Player player) {
	     // Start cleave animation
	     animationManager.startCleaveAnimation();
	
	     // Set pending cleave hit in collision handler
	     collisionHandler.setPendingCleaveHit(true);
	
	     // If player is in range, apply damage
	     if (collisionHandler.isPlayerInCleaveRange(player)) {
	         collisionHandler.applyCleaveHitToPlayer(player);
	     }
	 }
    

    public void render(SpriteBatch batch) {
        renderer.render(batch, bounds, animationManager.getCurrentFrame(), hp, maxHp, isDead);
    }

    public boolean isFacingRight() {
        return animationManager.isFacingRight();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isDead() {
        return isDead;
    }

    public float getHp() {

        return hp;
    }

    public float getAtk() {

        return atk;
    }

    public float getSpeed() {

        return speed;
    }

    public void takeHit(float damage) {
        if (isDead) return;

        this.hp = Math.max(0, this.hp - damage);

        // Trigger hit animation
        animationManager.startTakeHitAnimation();

        // Check for death
        if (this.hp == 0) {
            this.isDead = true;
            onDeath();
        }

        // Alert AI that monster was hit
        ai.onAggro();
    }


    @Override
    public void onCollision(Collidable other) {
        collisionHandler.handleCollision(other);
    }

    public void onCollision(Player player) {
        collisionHandler.handlePlayerCollision(player);
    }

    @Override
    public void onDeath() {
        // Base implementation is empty, subclasses can override
    	isDead = true;
		bounds.set(0, 0, 0, 0); // Reset position on death

    }


    public abstract void loadAnimations();

    protected void setupAnimations(
            Animation<TextureRegion> idleLeft, Animation<TextureRegion> idleRight,
            Animation<TextureRegion> walkLeft, Animation<TextureRegion> walkRight,
            Animation<TextureRegion> takeHitLeft, Animation<TextureRegion> takeHitRight,
            Animation<TextureRegion> cleaveLeft, Animation<TextureRegion> cleaveRight,
            Animation<TextureRegion> deathLeft, Animation<TextureRegion> deathRight) {

        animationManager.setAnimations(
            idleLeft, idleRight,
            walkLeft, walkRight,
            takeHitLeft, takeHitRight,
            cleaveLeft, cleaveRight,
            deathLeft, deathRight
        );
    }
    //Disposes of resources.
    public void dispose() {
        if (renderer != null) {
            renderer.dispose();
        }
    }
}