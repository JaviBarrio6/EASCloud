/* JOrbis
 * Copyright (C) 2000 ymnk, JCraft,Inc.
 *  
 * Written by: 2000 ymnk<ymnk@jcraft.com>
 *   
 * Many thanks to 
 *   Monty <monty@xiph.org> and 
 *   The XIPHOPHORUS Company http://www.xiph.org/ .
 * JOrbis has been based on their awesome works, Vorbis codec.
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
   
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.jcraft.jorbis;

import com.jcraft.jogg.*;

class Residue0 extends FuncResidue{

  Object unpack(Info vi, Buffer opb){
    int acc=0;
    InfoResidue0 info=new InfoResidue0();

    info.begin=opb.read(24);
    info.end=opb.read(24);
    info.grouping=opb.read(24)+1;
    info.partitions=opb.read(6)+1;
    info.groupbook=opb.read(8);

    for(int j=0;j<info.partitions;j++){
      int cascade=opb.read(3);
      if(opb.read(1)!=0){
        cascade|=(opb.read(5)<<3);
      }
      info.secondstages[j]=cascade;
      acc+=icount(cascade);
    }

    for(int j=0;j<acc;j++){
      info.booklist[j]=opb.read(8);
    }

    if(info.groupbook>=vi.books){
      free_info(info);
      return(null);
    }

    for(int j=0;j<acc;j++){
      if(info.booklist[j]>=vi.books){
	free_info(info);
	return(null);
      }
    }
    return(info);
  }

  void free_info(Object i){}

  private static int icount(int v){
    int ret=0;
   while(v!=0){
      ret+=(v&1);
      v>>>=1;
    }
    return(ret);
  }
}

class InfoResidue0{
  int begin;
  int end;


  int grouping;
  int partitions;
  int groupbook;
  int[] secondstages=new int[64];
  int[] booklist=new int[256];
}
