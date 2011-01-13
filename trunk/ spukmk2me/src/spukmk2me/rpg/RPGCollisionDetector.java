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

package spukmk2me.rpg;

public final class RPGCollisionDetector
{
    public RPGCollisionDetector( int numberOfCollidingGroup,
        boolean[] collidingTable )
    {        
        m_nCollidingGroup   = numberOfCollidingGroup;
        m_collidingTable    = collidingTable;
        m_fakedFirstNodes   = new RPGObject[ m_nCollidingGroup ];
        
        for ( int i = 0; i != m_nCollidingGroup; ++i )
        {
            m_fakedFirstNodes[ i ] = new RPGNullObject();
            m_fakedFirstNodes[ i ].c_colNext =
                m_fakedFirstNodes[ i ].c_colPrev = m_fakedFirstNodes[ i ];
        }
    }

    public void AddObject( RPGObject object, int collisionGroup )
    {
        //#ifdef __SPUKMK2ME_DEBUG
        if ( (object.c_colNext != null) || (object.c_colPrev != null) )
        {
            System.out.println( "Collision detector - Warning: Potentialy " +
                "defected node detected. Object: " + object.toString() );
        }
        //#endif

        object.c_colPrev =
            m_fakedFirstNodes[ collisionGroup ].c_colPrev;
        object.c_colNext = m_fakedFirstNodes[ collisionGroup ];
        m_fakedFirstNodes[ collisionGroup ].c_colPrev.c_colNext =
            object;
        m_fakedFirstNodes[ collisionGroup ].c_colPrev = object;
    }

    public void CheckForCollision()
    {
        int i, j, collidingIndex = 0;
        RPGObject iterator1, iterator2;

        for ( i = 0; i != m_nCollidingGroup; ++i )
        {
            for ( j = 0; j != m_nCollidingGroup; ++j )
            {
                if ( m_collidingTable[ collidingIndex ] )
                {
                    for ( iterator1 = m_fakedFirstNodes[ i ].c_colNext;
                        iterator1 != m_fakedFirstNodes[ i ];
                        iterator1 = iterator1.c_colNext )
                    {
                        for ( iterator2 = m_fakedFirstNodes[ j ].c_colNext;
                            iterator2 != m_fakedFirstNodes[ j ];
                            iterator2 = iterator2.c_colNext )
                        {
                            if ( iterator2.AffectableBy( iterator1 ) )
                            {
                                if ( IsCollide( iterator1, iterator2 ) )
                                    iterator1.Affect( iterator2 );
                            }
                        }
                    }
                }

                ++collidingIndex;
            }
        }
    }

    private static boolean IsCollide( RPGObject obj1, RPGObject obj2 )
    {
        if ( obj1.c_x > obj2.c_x + obj2.c_width - 1 )
            return false;

        if ( obj2.c_x > obj1.c_x + obj1.c_width - 1 )
            return false;

        if ( obj1.c_y > obj2.c_y + obj2.c_length - 1 )
            return false;

        if ( obj2.c_y > obj1.c_y + obj1.c_length - 1 )
            return false;

        if ( obj1.c_z > obj2.c_z + obj2.c_height - 1 )
            return false;

        if ( obj2.c_z > obj1.c_z + obj1.c_height - 1 )
            return false;

        return true;
        /*return  (obj1.c_x <= obj2.c_x + obj2.c_width) &&
                (obj2.c_x <= obj1.c_x + obj1.c_width) &&
                (obj1.c_y <= obj2.c_y + obj2.c_length) &&
                (obj2.c_y <= obj1.c_y + obj1.c_length) &&
                (obj1.c_z <= obj2.c_z + obj2.c_length) &&
                (obj2.c_z <= obj1.c_z + obj1.c_length);
        */
    }

    private RPGObject[] m_fakedFirstNodes;
    private boolean[]   m_collidingTable;
    private int         m_nCollidingGroup;
}
