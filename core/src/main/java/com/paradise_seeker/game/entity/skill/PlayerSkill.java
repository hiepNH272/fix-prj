package com.paradise_seeker.game.entity.skill;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.paradise_seeker.game.entity.Character;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;
import com.paradise_seeker.game.entity.monster.Monster;


import java.util.HashMap;
import java.util.Map;

public abstract class PlayerSkill implements Skill {
    protected float manaCost;
    protected long cooldown;
    protected long lastUsedTime;
    protected Map<String, Animation<TextureRegion>> skillAnimations = new HashMap<>();
    protected float damageMultiplier = 1.0f;

    public PlayerSkill(float manaCost, long cooldown) {
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.lastUsedTime = 0;
    }

    public boolean canUse(long now) {
        return (now - lastUsedTime) >= cooldown;
    }

    public float getManaCost() {
        return manaCost;
    }

    public void setLastUsedTime(long time) {
        this.lastUsedTime = time;
    }

    public void setDamageMultiplier(float multiplier) {
        this.damageMultiplier = multiplier;
    }

    public float getdamageMultiplier() {
        return damageMultiplier;
    }

    protected abstract void loadSkillAnimations();

    // Không chứa logic cụ thể, để các lớp con override
    @Override
    public abstract void castSkill(float atk, float x, float y, String direction);

    @Override
    public abstract void castSkill(float atk, Rectangle bounds, String direction);

    @Override
    public void execute(Character target) { /* Có thể để trống nếu không cần */ }

    @Override
    public void castSkill(float atk, float x, float y) {}

    @Override
    public void castSkill(float atk, Character target) {}

    @Override
    public void update(long now) {
    }
    public void render(SpriteBatch batch) {
        // Mặc định không làm gì. Override ở lớp con nếu cần hiển thị skill
    }
    public void updatePosition(float x, float y) {
        // Không làm gì mặc định
    }

    public void updateSkill(float delta, List<Monster> monsters) {
        // Không làm gì mặc định
    }
}
