/* -*-mode:java; c-basic-offset:2; -*- */
/* JRoar -- pure Java streaming server for Ogg 
 *
 * Copyright (C) 2001,2002 ymnk, JCraft,Inc.
 *
 * Written by: 2001,2002 ymnk<ymnk@jcraft.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.jcraft.jroar;
import java.lang.*;
import java.io.*;
import java.util.*;

class M3u extends Page{
  String pls;

  M3u(String pls){
    super();
    this.pls=pls;
  }
  public void kick(MySocket ms, Hashtable<?, ?> vars, Vector<?> h) throws IOException{
    byte[] foo=pls.getBytes();
    foo[foo.length-1]='g'; foo[foo.length-2]='g'; foo[foo.length-3]='o';
    String ogg=new String(foo);
    Source source=Source.getSource(ogg);
    if(source==null){
      foo[foo.length-1]='x'; foo[foo.length-2]='p'; foo[foo.length-3]='s';
      ogg=new String(foo);
      source=Source.getSource(ogg);
    }
    if(source!=null){
      ms.println("HTTP/1.0 200 OK") ;
      ms.println("Connection: close") ;
      ms.println("Content-Type: audio/x-mpegurl") ;
      ms.println("") ;
      ms.println(HttpServer.myURL+ogg);
      ms.flush( ) ;
      ms.close( ) ;
    }
    else{
      notFound(ms);
    }
  }
}
