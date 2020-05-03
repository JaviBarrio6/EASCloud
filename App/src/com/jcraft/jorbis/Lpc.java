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

class Lpc{
  Drft fft=new Drft();;

  int ln;
  int m;

  static float lpc_from_data(float[] data, float[] lpc,int n,int m){
    float[] aut=new float[m+1];
    float error;
    int i,j;

    j=m+1;
    while(j--!=0){
      float d=0;
      for(i=j;i<n;i++)d+=data[i]*data[i-j];
      aut[j]=d;
    }
    error=aut[0];
  
    for(i=0;i<m;i++){
      float r=-aut[i+1];

      if(error==0){
        for(int k=0; k<m; k++) lpc[k]=0.0f;
        return 0;
      }

      for(j=0;j<i;j++)r-=lpc[j]*aut[i-j];
      r/=error; 

    
      lpc[i]=r;
      for(j=0;j<i/2;j++){
        float tmp=lpc[j];
        lpc[j]+=r*lpc[i-1-j];
        lpc[i-1-j]+=r*tmp;
        }
      if(i%2!=0)lpc[j]+=lpc[j]*r;
    
      error*=1.0-r*r;
    }
    return error;
  }

  void init(int mapped, int m){
    ln=mapped;
    this.m=m;
    fft.init(mapped*2);
  }

  void clear(){
    fft.clear();
  }

  static float FAST_HYPOT(float a, float b){
    return (float)Math.sqrt((a)*(a) + (b)*(b));
  }

  void lpc_to_curve(float[] curve, float[] lpc, float amp){

    for(int i=0; i<ln*2; i++)curve[i]=0.0f;
    if(amp==0)return;
    for(int i=0;i<m;i++){
      curve[i*2+1]=lpc[i]/(4*amp);
      curve[i*2+2]=-lpc[i]/(4*amp);
    }

    fft.backward(curve);

    {
      int l2=ln*2;
      float unit=(float)(1./amp);
      curve[0]=(float)(1./(curve[0]*2+unit));
      for(int i=1;i<ln;i++){
          float real=(curve[i]+curve[l2-i]);
          float imag=(curve[i]-curve[l2-i]);

          float a = real + unit;
          curve[i] = (float)(1.0 / FAST_HYPOT(a, imag));
      }
    }
  }
}
