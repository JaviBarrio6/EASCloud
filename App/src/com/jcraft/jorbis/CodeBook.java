
package com.jcraft.jorbis;

import com.jcraft.jogg.*;

class CodeBook{
  int dim;            // codebook dimensions (elements per vector)
  float[] valuelist; // list of dim*entries actual entry values
  DecodeAux decode_tree;


  // One the encode side, our vector writers are each designed for a
  // specific purpose, and the encoder is not flexible without modification:
  // 
  // The LSP vector coder uses a single stage nearest-match with no
  // interleave, so no step and no error return.  This is specced by floor0
  // and doesn't change.
  // 
  // Residue0 encoding interleaves, uses multiple stages, and each stage
  // peels of a specific amount of resolution from a lattice (thus we want
  // to match by threshhold, not nearest match).  Residue doesn't *have* to
  // be encoded that way, but to change it, one will need to add more
  // infrastructure on the encode side (decode side is specced and simpler)

  private int[] t=new int[15];  // decodevs_add is synchronized for re-using t.
  synchronized int decodevs_add(float[]a, int offset, Buffer b, int n){
    int step=n/dim;
    int entry;
    int i,j,o;

    if(t.length<step){
      t=new int[step];
    }

    for(i = 0; i < step; i++){
      entry=decode(b);
      if(entry==-1)return(-1);
      t[i]=entry*dim;
    }
    for(i=0,o=0;i<dim;i++,o+=step){
      for(j=0;j<step;j++){
	a[offset+o+j]+=valuelist[t[j]+i];
      }
    }

    return(0);
  }

  int decodev_add(float[]a, int offset, Buffer b,int n){
    int i,j,entry;
    int t;

    if(dim>8){
      for(i=0;i<n;){
        entry = decode(b);
        if(entry==-1)return(-1);
	t=entry*dim;
	for(j=0;j<dim;){
	  a[offset+(i++)]+=valuelist[t+(j++)];
	}
      }
    }
    else{
      for(i=0;i<n;){
	entry=decode(b);
	if(entry==-1)return(-1);
	t=entry*dim;
	j=0;
	switch(dim){
	case 8:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 7:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 6:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 5:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 4:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 3:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 2:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 1:
	  a[offset+(i++)]+=valuelist[t+(j++)];
	case 0:
	break;
	}
      }
    }    
    return(0);
  }

  int decodev_set(float[] a,int offset, Buffer b, int n){
    int i,j,entry;
    int t;

    for(i=0;i<n;){
      entry = decode(b);
      if(entry==-1)return(-1);
      t=entry*dim;
      for(j=0;j<dim;){
        a[offset+i++]=valuelist[t+(j++)];
      }
    }
    return(0);
  }

  int decodevv_add(float[][] a, int offset,int ch, Buffer b,int n){
    int i,j,k,entry;
    int chptr=0;

    for(i=offset/ch;i<(offset+n)/ch;){
      entry = decode(b);
      if(entry==-1)return(-1);

      int t = entry*dim;
      for(j=0;j<dim;j++){
        a[chptr++][i]+=valuelist[t+j];
        if(chptr==ch){
          chptr=0;
	  i++;
	}
      }
    }
    return(0);
  }


  // returns the entry number or -1 on eof
  int decode(Buffer b){
    int ptr=0;
    DecodeAux t=decode_tree;
    int lok=b.look(t.tabn);

    if(lok>=0){
      ptr=t.tab[lok];
      b.adv(t.tabl[lok]);
      if(ptr<=0){
        return -ptr;
      }
    }
    do{
      switch(b.read1()){
      case 0:
	ptr=t.ptr0[ptr];
	break;
      case 1:
	ptr=t.ptr1[ptr];
	break;
      case -1:
      default:
	return(-1);
      }
    }
    while(ptr>0);
    return(-ptr);
  }

}

class DecodeAux{
  int[] tab;
  int[] tabl;
  int tabn;

  int[] ptr0;
  int[] ptr1;
}
