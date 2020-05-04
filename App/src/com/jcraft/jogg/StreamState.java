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

package com.jcraft.jogg;

import java.util.Arrays;

public class StreamState{
  byte[] body_data;
  int body_storage;
  int body_fill;
private  int body_returned;


  int[] lacing_vals;
  long[] granule_vals;
  int lacing_storage;
  int lacing_fill;
  int lacing_packet;
  int lacing_returned;

  byte[] header=new byte[282];
  int header_fill;

  public int e_o_s;
  int b_o_s;
  int serialno;
  int pageno;
  long packetno;
  long granulepos;

  public StreamState(){
    init();
  }

  void init(){
    body_storage=16*1024;
    body_data=new byte[body_storage];
    lacing_storage=1024;
    lacing_vals=new int[lacing_storage];
    granule_vals=new long[lacing_storage];
  }
  public void init(int serialno){
    if(body_data==null){ init(); }
    else{
      Arrays.fill(body_data, (byte) 0);
      Arrays.fill(lacing_vals, 0);
      Arrays.fill(granule_vals, 0);
    }
    this.serialno=serialno;
  }

  void body_expand(int needed){
    if(body_storage<=body_fill+needed){
      body_storage+=(needed+1024);
      byte[] foo=new byte[body_storage];
      System.arraycopy(body_data, 0, foo, 0, body_data.length);
      body_data=foo;
    }
  }
  void lacing_expand(int needed){
    if(lacing_storage<=lacing_fill+needed){
      lacing_storage+=(needed+32);
      int[] foo=new int[lacing_storage];
      System.arraycopy(lacing_vals, 0, foo, 0, lacing_vals.length);
      lacing_vals=foo;

      long[] bar=new long[lacing_storage];
      System.arraycopy(granule_vals, 0, bar, 0, granule_vals.length);
      granule_vals=bar;
    }
  }

  public int packetout(Packet op){

    int ptr=lacing_returned;

    if(lacing_packet<=ptr){
      return(0);
    }

    if((lacing_vals[ptr]&0x400)!=0){
      lacing_returned++;
      packetno++;
      return(-1);
    }

    {
      int size=lacing_vals[ptr]&0xff;
      int bytes=0;

      op.packet_base=body_data;
      op.packet=body_returned;
      op.e_o_s=lacing_vals[ptr]&0x200;
      op.b_o_s=lacing_vals[ptr]&0x100;
      bytes+=size;

      while(size==255){
	int val=lacing_vals[++ptr];
	size=val&0xff;
	if((val&0x200)!=0)op.e_o_s=0x200;
	bytes+=size;
      }

      op.packetno=packetno;
      op.granulepos=granule_vals[ptr];
      op.bytes=bytes;
      body_returned+=bytes;
      lacing_returned=ptr+1;
    }
    packetno++;
    return(1);
  }

  public int pagein(Page og){
    byte[] header_base=og.header_base;
    int header=og.header;
    byte[] body_base=og.body_base;
    int body=og.body;
    int bodysize=og.body_len;
    int segptr=0;

    int version=og.version();
    int continued=og.continued();
    int bos=og.bos();
    int eos=og.eos();
    long granulepos=og.granulepos();
    int _serialno=og.serialno();
    int _pageno=og.pageno();
    int segments=header_base[header+26]&0xff;

    {
      int lr=lacing_returned;
      int br=body_returned;

      if(br!=0){
        body_fill-=br;
        if(body_fill!=0){
	  System.arraycopy(body_data, br, body_data, 0, body_fill);
	}
	body_returned=0;
      }

      if(lr!=0){

	if((lacing_fill-lr)!=0){
	  System.arraycopy(lacing_vals, lr, lacing_vals, 0, lacing_fill-lr);
	  System.arraycopy(granule_vals, lr, granule_vals, 0, lacing_fill-lr);
	}
	lacing_fill-=lr;
	lacing_packet-=lr;
	lacing_returned=0;
      }
    }

    if(_serialno!=serialno)return(-1);
    if(version>0)return(-1);

    lacing_expand(segments+1);

    if(_pageno!=pageno){
      int i;

      for(i=lacing_packet;i<lacing_fill;i++){
	body_fill-=lacing_vals[i]&0xff;
      }
      lacing_fill=lacing_packet;

      if(pageno!=-1){
	lacing_vals[lacing_fill++]=0x400;
	lacing_packet++;
      }

      if(continued!=0){
	bos=0;
	for(;segptr<segments;segptr++){
	  int val=(header_base[header+27+segptr]&0xff);
	  body+=val;
	  bodysize-=val;
	  if(val<255){
	    segptr++;
	    break;
	  }
	}
      }
    }

    if(bodysize!=0){
      body_expand(bodysize);
      System.arraycopy(body_base, body, body_data, body_fill, bodysize);
      body_fill+=bodysize;
    }


    {
      int saved=-1;
      while(segptr<segments){
	int val=(header_base[header+27+segptr]&0xff);
	lacing_vals[lacing_fill]=val;
	granule_vals[lacing_fill]=-1;
      
	if(bos!=0){
	  lacing_vals[lacing_fill]|=0x100;
	  bos=0;
	}
      
	if(val<255)saved=lacing_fill;
      
	lacing_fill++;
	segptr++;
      
	if(val<255)lacing_packet=lacing_fill;
      }

      if(saved!=-1){
	granule_vals[saved]=granulepos;
      }
    }

    if(eos!=0){
      e_o_s=1;
      if(lacing_fill>0)
	lacing_vals[lacing_fill-1]|=0x200;
    }

    pageno=_pageno+1;
    return(0);
  }

  public int flush(Page og){


    int i;
    int vals;
    int maxvals=(Math.min(lacing_fill, 255));
    int bytes=0;
    int acc=0;
    long granule_pos=granule_vals[0];

    if(maxvals==0)return(0);
    if(b_o_s==0){
      granule_pos=0;
      for(vals=0;vals<maxvals;vals++){
        if((lacing_vals[vals]&0x0ff)<255){
	  vals++;
	  break;
        }
      }
    }
    else{
      for(vals=0;vals<maxvals;vals++){
        if(acc>4096)break;
        acc+=(lacing_vals[vals]&0x0ff);
        granule_pos=granule_vals[vals];
      }
    }
    System.arraycopy("OggS".getBytes(), 0, header, 0, 4);
    header[4]=0x00;

    header[5]=0x00;
    if((lacing_vals[0]&0x100)==0)header[5]|=0x01;
    if(b_o_s==0) header[5]|=0x02;
    if(e_o_s!=0 && lacing_fill==vals) header[5]|=0x04;
    b_o_s=1;


    for(i=6;i<14;i++){
      header[i]=(byte)granule_pos;
      granule_pos>>>=8;
    }

    {
      int _serialno=serialno;
      for(i=14;i<18;i++){
        header[i]=(byte)_serialno;
        _serialno>>>=8;
      }
    }

    if(pageno==-1)pageno=0;
    {
      int _pageno=pageno++;
      for(i=18;i<22;i++){
        header[i]=(byte)_pageno;
        _pageno>>>=8;
      }
    }
    header[22]=0;
    header[23]=0;
    header[24]=0;
    header[25]=0;

    header[26]=(byte)vals;
    for(i=0;i<vals;i++){
      header[i+27]=(byte)lacing_vals[i];
      bytes+=(header[i+27]&0xff);
    }

    og.header_base=header;
    og.header=0;
    og.header_len=header_fill=vals+27;
    og.body_base=body_data;
    og.body=body_returned;
    og.body_len=bytes;

    lacing_fill-=vals;
    System.arraycopy(lacing_vals, vals, lacing_vals, 0, lacing_fill*4);
    System.arraycopy(granule_vals, vals, granule_vals, 0, lacing_fill*8);
    body_returned+=bytes;

    og.checksum();

    return(1);
  }

  public void reset(){
    body_fill=0;
    body_returned=0;

    lacing_fill=0;
    lacing_packet=0;
    lacing_returned=0;

    header_fill=0;

    e_o_s=0;
    b_o_s=0;
    pageno=-1;
    packetno=0;
    granulepos=0;
  }
}
