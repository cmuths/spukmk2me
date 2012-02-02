/*
 *  SPUKMK2me - SPUKMK2 Engine for J2ME platform
 *  Copyright 2010 - 2011  HNYD Team
 *
 *   SPUKMK2me is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *   SPUKMK2me is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *  along with SPUKMK2me.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spukmk2me.gameflow;

import com.spukmk2me.Device;

/**
 *  Interface for game processor.
 */
public interface IGameProcessor
{
    /**
     *  Your game processor do every initialisation here, not in constructors.
     *  \details This function will be called once by GameThread.\n
     * Most of construction code should be placed here, you shouldn't place
     * them in your constructor since game processor must be initialized from
     * the very beginning of application's lifetime, and J2ME creators advised
     * us not to do so many thing in application startup period, so you should
     * delay your initialization to this function.\n
     *  After initialisation, this function must return the SPUKMK2me Device
     * which will be used later by the game.
     */
    public Device Init( ConfigData configData );

    /**
     *  Process the games periodically.
     *  \details This is where you put your game processing code.
     *  @param deltaTime The amount of time passed since the last call to this
     * function in milliseconds. The time passed to this function doesn't
     * surpass the value defined in ConfigData.MAX_MS_PER_FRAME.\n
     */
    public void ProcessGame( long deltaMilliseconds );

    /**
     *  Make this function return true if you want to shutdown your game.
     *  \details Like ProcessGame(), this function is called periodically.
     *  @return True if and only if you want to close the game.
     */
    public boolean IsFinished();
}
