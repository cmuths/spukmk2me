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

public final class BranchingManager
{
    public BranchingManager( int maxStack )
    {        
        m_maxStack  = maxStack;
        m_stack     = new int[ m_maxStack ];
        m_nStack    = 0;
    }

    public void ImportWorkers( IBranching[] workers )
    {
        m_workers = workers;
    }

    public void BranchWork()
    {
        if ( m_nStack == 0 )
            return;

        int currentIndex = m_nStack - 1;

        if ( !m_workers[ m_stack[ currentIndex ] ].IsFinished() )
        {
            m_workers[ m_stack[ currentIndex ] ].DoWork();

            byte chained =
                m_workers[ m_stack[ currentIndex ] ].GetChainedWork();
            
            if ( chained != IBranching.CHAIN_NOTHING )
            {
                ChainWork( chained );
            }
        }

        if ( m_workers[ m_stack[ currentIndex ] ].IsFinished() )
        {
            for ( ; currentIndex < m_nStack - 1; ++currentIndex )
                m_stack[ currentIndex ] = m_stack[ currentIndex + 1 ];

            --m_nStack;
        }
    }

    public void ChainWork( int workIndex )
    {
        m_stack[ m_nStack++ ] = workIndex;
        m_workers[ workIndex ].Chained();
    }

    private IBranching[]    m_workers;
    private int[]           m_stack;
    private int             m_maxStack, m_nStack;
}