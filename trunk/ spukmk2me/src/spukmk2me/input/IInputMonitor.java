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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package spukmk2me.input;

/**
 *  Wrapper for input management.
 *  \details Since there is some quite common problems happen when developing
 * games on J2ME platform using MIDP API, so I decided to put up a wrapper for
 * input management. Hope that helps.\n
 */
public interface IInputMonitor
{    
    /**
     *  Setup the input receiving mode.
     *  \details You can combine the input mode from INPUTMODE_KEY and
     * INPUTMODE_TOUCH. Or even disable all input signal via the InputMonitor
     * by passing INPUTMODE_NONE.
     *  @param inputMode The input mode.
     */
    public void SetInputMode( byte inputMode );

    /**
     *  Get the bit pattern for actions.
     *  \details The key waiting time will be reseted to the maximum value (the
     * value passed to SetLatency())
     *  @return The bit pattern for actions. See the constants list for more
     * details.
     */
    public int GetActionBitPattern();

    /**
     *  Get the touch position.
     *  \details The X coordinate will be stored at the higher word of returned
     * value, Y coordinate will be the lower word. The value 0xFFFFFFFF will be
     * returned if there is no touching action.
     *  @return The data for touching position.
     */
    public int GetTouchingPosition();

    /**
     *  Change the key input latency.
     *  \details Latency? What's that?\n
     *  Have you ever suffered this situation: when you've just pressed
     * the "right" button, your character immediately moves at
     * super-man speed to the enemies and died in a good way? That's because
     * you didn't time-based simulate your game well enough. But there is
     * another minor cause: you catch the key signals too frequently, or even
     * catch them as much as possible in every frame. That why this
     * "key latency" was born.
     *  @param latency The key latency, measured in milliseconds.
     */
    public void SetLatency( long latency );

    /**
     *  Set the latency that has effect only once.
     *  \details Isn't like SetLatency(), this latency here is only
     * temporary. To be more specific, let's say your games are setting
     * the key latency to 100ms, but after players press "Quit" button, you
     * want to hold down the key little with 500ms latency so players don't
     * accidently press the next
     * "YEEES, I really want to quit this stupid game" button (100ms is still
     * little too fast, you know). You want 500ms latency has only one turn of
     * effect, right. If that's true, so use this function.
     *  @param latency The temporary latency, measured in milliseconds.
     */
    public void SetTemporaryLatency( long latency );    

    public static final int ACT_NONE    = 0x00000000; //!< Nothing.
    public static final int ACT_UP      = 0x00000001; //!< Up or Numpad 2
    public static final int ACT_LEFT    = 0x00000002; //!< Left or Numpad 4
    public static final int ACT_DOWN    = 0x00000004; //!< Right or Numpad 8
    public static final int ACT_RIGHT   = 0x00000008; //!< Left or Numpad 6
    public static final int ACT_FIRE    = 0x00000010; //!< Mid or Numpad 5
    public static final int ACT_LSOFT   = 0x00000020; //!< Left button
    public static final int ACT_RSOFT   = 0x00000040; //!< Right button

    public static final byte INPUTMODE_NONE     = 0x00; //!< Disable the input.
    public static final byte INPUTMODE_KEY      = 0x01; //!< Accept keys.
    public static final byte INPUTMODE_TOUCH    = 0x02; //!< Accept touching.
}
