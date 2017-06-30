/*
 * Copyright 2011-2013 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package com.rojel.wesv;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sk89q.worldedit.regions.Region;

/**
 * A custom "WorldEditSelectionChange" Bukkit event class.
 *
 * @author  rojel
 * @author  Martin Ambrus
 * @since   1.0a
 */
public class WorldEditSelectionChangeEvent extends Event {
    
    /**
     * A list of all handlers that listen for this event.
     */
    private static final HandlerList handlers = new HandlerList();
    
    /**
     * A player for who to listen to this event.
     */
    private final Player player;
    
    /**
     * WorldEdit region for this event.
     */
    private final Region region;

    /**
     * Constructor.
     * Creates a new custom "WorldEditSelectionChange" event.
     * 
     * @param player The player for who to listen to this event.
     * @param region The region in which to listen to this event.
     */
    public WorldEditSelectionChangeEvent(final Player player, final Region region) {
        super();
        this.player = player;
        this.region = region;
    }

    /**
     * Gets a list of handlers for this event.
     * 
     * @return Returns list of handlers which listen to this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets a list of handlers for this event.
     * 
     * @return Returns list of handlers which listen to this event.
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets a player for who listening to this event is enabled.
     * 
     * @return Returns player for who listening to this event is enabled.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets a region in which listening to this event is enabled.
     * 
     * @return Returns region in which listening to this event is enabled.
     */
    public Region getRegion() {
        return this.region;
    }
}
