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

class StaticCodeBook {
    int dim;
    int entries;
    int[] lengthlist;

    int maptype;

    int q_min;
    int q_delta;
    int q_quant;
    int q_sequencep;


    int[] quantlist;


    StaticCodeBook() {}

    int pack(Buffer opb) {
        int i;
        boolean ordered = false;

        opb.write(0x564342, 24);
        opb.write(dim, 16);
        opb.write(entries, 24);


        for (i = 1; i < entries; i++) {
            if (lengthlist[i] < lengthlist[i - 1]) break;
        }
        if (i == entries) ordered = true;

        if (ordered) {
            int count = 0;
            opb.write(1, 1);
            opb.write(lengthlist[0] - 1, 5);

            for (i = 1; i < entries; i++) {
                int _this = lengthlist[i];
                int _last = lengthlist[i - 1];
                if (_this > _last) {
                    for (int j = _last; j < _this; j++) {
                        opb.write(i - count, ilog(entries - count));
                        count = i;
                    }
                }
            }
            opb.write(i - count, ilog(entries - count));
        } else {

            opb.write(0, 1);

            for (i = 0; i < entries; i++) {
                if (lengthlist[i] == 0) break;
            }

            if (i == entries) {
                opb.write(0, 1); // no unused entries
                for (i = 0; i < entries; i++) {
                    opb.write(lengthlist[i] - 1, 5);
                }
            } else {
                opb.write(1, 1); // we have unused entries; thus we tag
                for (i = 0; i < entries; i++) {
                    if (lengthlist[i] == 0) {
                        opb.write(0, 1);
                    } else {
                        opb.write(1, 1);
                        opb.write(lengthlist[i] - 1, 5);
                    }
                }
            }
        }

        opb.write(maptype, 4);
        switch (maptype) {
            case 0:
                break;
            case 1:
            case 2:
                if (quantlist == null) {
                    return (-1);
                }

                opb.write(q_min, 32);
                opb.write(q_delta, 32);
                opb.write(q_quant - 1, 4);
                opb.write(q_sequencep, 1);

            {
                int quantvals = 0;
                switch (maptype) {
                    case 1:
                        quantvals = maptype1_quantvals();
                        break;
                    case 2:
                        quantvals = entries * dim;
                        break;
                }

                for (i = 0; i < quantvals; i++) {
                    opb.write(Math.abs(quantlist[i]), q_quant);
                }
            }
            break;
            default:

                return (-1);
        }
        return (0);
    }

    int unpack(Buffer opb) {
        int i;

        if (opb.read(24) != 0x564342) {
            clear();
            return (-1);
        }

        dim = opb.read(16);
        entries = opb.read(24);
        if (entries == -1) {
            clear();
            return (-1);
        }

        switch (opb.read(1)) {
            case 0:

                lengthlist = new int[entries];


                if (opb.read(1) != 0) {


                    for (i = 0; i < entries; i++) {
                        if (opb.read(1) != 0) {
                            int num = opb.read(5);
                            if (num == -1) {
                                clear();
                                return (-1);
                            }
                            lengthlist[i] = num + 1;
                        } else {
                            lengthlist[i] = 0;
                        }
                    }
                } else {
                    for (i = 0; i < entries; i++) {
                        int num = opb.read(5);
                        if (num == -1) {
                            clear();
                            return (-1);
                        }
                        lengthlist[i] = num + 1;
                    }
                }
                break;
            case 1: {
                int length = opb.read(5) + 1;
                lengthlist = new int[entries];

                for (i = 0; i < entries; ) {
                    int num = opb.read(ilog(entries - i));
                    if (num == -1) {
                        clear();
                        return (-1);
                    }
                    for (int j = 0; j < num; j++, i++) {
                        lengthlist[i] = length;
                    }
                    length++;
                }
            }
            break;
            default:
                // EOF
                return (-1);
        }
        switch ((maptype = opb.read(4))) {
            case 0:
                break;
            case 1:
            case 2:
                q_min = opb.read(32);
                q_delta = opb.read(32);
                q_quant = opb.read(4) + 1;
                q_sequencep = opb.read(1);

            {
                int quantvals = 0;
                switch (maptype) {
                    case 1:
                        quantvals = maptype1_quantvals();
                        break;
                    case 2:
                        quantvals = entries * dim;
                        break;
                }

                quantlist = new int[quantvals];
                for (i = 0; i < quantvals; i++) {
                    quantlist[i] = opb.read(q_quant);
                }
                if (quantlist[quantvals - 1] == -1) {
                    clear();
                    return (-1);
                }
            }
            break;
            default:
                clear();
                return (-1);
        }
        return (0);
    }

    private int maptype1_quantvals() {
        int vals = (int) (Math.floor(Math.pow(entries, 1. / dim)));

        while (true) {
            int acc = 1;
            int acc1 = 1;
            for (int i = 0; i < dim; i++) {
                acc *= vals;
                acc1 *= vals + 1;
            }
            if (acc <= entries && acc1 > entries) {
                return (vals);
            } else {
                if (acc > entries) {
                    vals--;
                } else {
                    vals++;
                }
            }
        }
    }

    void clear() {
    }

    private static int ilog(int v) {
        int ret = 0;
        while (v != 0) {
            ret++;
            v >>>= 1;
        }
        return (ret);
    }

}




