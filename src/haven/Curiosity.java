/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import static haven.Text.numfnd;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Curiosity extends ItemInfo.Tip {
    public final int exp, mw, enc;

    public Curiosity(Owner owner, int exp, int mw, int enc) {
        super(owner);
        this.exp = exp;
        this.mw = mw;
        this.enc = enc;
    }
    
    private String LPH(){
    	Resource res;
        try {
            res = ((GItem)owner).resource();
        } catch (Loading l) {
            return "";
        }
        
        Double st = CurioStudyTimes.curios.get( res.basename() );
        if (st == null)
            return "";
        
        int studytime = (int) (st * 60);
        int hours = (int) (studytime / 60);
        int minutes = studytime - hours * 60;
        int lph = (int)(exp/st);

        if(minutes == 0) return String.format(Resource.getLocString(Resource.BUNDLE_LABEL, "Time: Hours $col[255,192,255]{%d}\nLP per hour: $col[255,192,255]{%d}\n"), hours, lph);
        return String.format(Resource.getLocString(Resource.BUNDLE_LABEL, "Time: Hours $col[255,192,255]{%d} Minutes $col[255,192,255]{%d}\nLP per hour: $col[255,192,255]{%d}\n"), hours, minutes, lph);
    }

    public BufferedImage tipimg() {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format(Resource.getLocString(Resource.BUNDLE_LABEL, "Learning points: $col[192,192,255]{%s}\nMental weight: $col[255,192,255]{%d}\n"), Utils.thformat(exp), mw));
        buf.append(LPH() );
        if (enc > 0)
            buf.append(String.format(Resource.getLocString(Resource.BUNDLE_LABEL, "Experience cost: $col[255,255,192]{%d}\n"), enc));
        return (RichText.render(buf.toString(), 0).img);
    }
}
