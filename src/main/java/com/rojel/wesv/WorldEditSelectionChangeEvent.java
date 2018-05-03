/***
* The MIT License
* 
* Copyright (c) 2010-2017 Google, Inc. http://angularjs.org
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/
package com.rojel.wesv;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sk89q.worldedit.regions.Region;

/**
 * A custom "WorldEditSelectionChange" Bukkit event class.
 *
 * @author rojel
 * @author Martin Ambrus
 * @since 1.0a
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
	 * Constructor. Creates a new custom "WorldEditSelectionChange" event.
	 * 
	 * @param player
	 *            The player for who to listen to this event.
	 * @param region
	 *            The region in which to listen to this event.
	 */
	public WorldEditSelectionChangeEvent(final Player player, final Region region) {
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
