package haven;

import javax.media.opengl.GL2;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;

import static haven.MCache.tilesz;

public class TileOutline implements Rendered {
    private final MCache map;
    private final FloatBuffer[] vertexBuffers;
    private final int area;
    private final States.ColState color;
    private Location location;
    private Coord ul;
    private int curIndex;

    public TileOutline(MCache map) {
        this.map = map;
        this.area = (MCache.cutsz.x * 5) * (MCache.cutsz.y * 5);
        this.color = new States.ColState(255, 255, 255, 64);
        // double-buffer to prevent flickering
        vertexBuffers = new FloatBuffer[2];
        vertexBuffers[0] = Utils.mkfbuf(this.area * 3 * 4);
        vertexBuffers[1] = Utils.mkfbuf(this.area * 3 * 4);
        curIndex = 0;
    }

    @Override
    public void draw(GOut g) {
        g.apply();
        BGL gl = g.gl;
        FloatBuffer vbuf = getCurrentBuffer();
        vbuf.rewind();
        gl.glLineWidth(1.0F);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vbuf);
        gl.glDrawArrays(GL2.GL_LINES, 0, area * 4);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public boolean setup(RenderList rl) {
        rl.prepo(location);
        rl.prepo(States.ndepthtest);
        rl.prepo(last);
        rl.prepo(color);
        return true;
    }

    public void update(Coord ul) {
        try {
            this.ul = ul;
            this.location = Location.xlate(new Coord3f((float) (ul.x * tilesz.x), (float) (-ul.y * tilesz.y), 0.0F));
            swapBuffers();
            Coord c = new Coord();
            Coord size = ul.add(MCache.cutsz.mul(5));
            for (c.y = ul.y; c.y < size.y; c.y++)
                for (c.x = ul.x; c.x < size.x; c.x++)
                    addLineStrip(mapToScreen(c), mapToScreen(c.add(1, 0)), mapToScreen(c.add(1, 1)));
        } catch (Loading e) {
        }
    }

    private Coord3f mapToScreen(Coord c) {
        return new Coord3f((float) ((c.x - ul.x) * tilesz.x), (float) (-(c.y - ul.y) * tilesz.y), map.getz(c));
    }
    
    public void updateServergrid(Coord tc, Coord ul) {
        try {
            this.ul = ul;
            this.location = Location.xlate(new Coord3f((float) ul.x, (float) -ul.y, 0.0F));
            swapBuffers();
            Coord c = new Coord();
            tc = tc.mul(11);
            Coord size = tc.add(MCache.cutsz.mul(55));
            ul = ul.sub(1100, 1100);
            System.out.println(tc + " " + ul + " " + size);
            //addLineStrip(mapToScreenServergrid(ul), mapToScreenServergrid(tc));
            //addLineStrip(mapToScreenServergrid(size), mapToScreenServergrid(tc));
            for (c.y = ul.y; c.y < size.y; c.y += 11){
            	if(c.y < tc.y) continue;
                for (c.x = ul.x; c.x < size.x; c.x += 100){
                	if(c.x < tc.x) continue;
                	addLineStrip(mapToScreenServergrid(c), mapToScreenServergrid(c.add(0, 11)) );
                }
            }
            for (c.x = ul.x; c.x < size.x; c.x += 11){
            	if(c.x < tc.x) continue;
            	for (c.y = ul.y; c.y < size.y; c.y += 100){
            		if(c.y < tc.y) continue;
                	addLineStrip(mapToScreenServergrid(c), mapToScreenServergrid(c.add(11, 0)) );
        		}
    		}
        } catch (Loading e) {
        	System.out.println(e);
        }
    }

    private Coord3f mapToScreenServergrid(Coord c) {
        return new Coord3f((float)(c.x - ul.x), (float) -(c.y - ul.y), (float) map.getcz(c));
    }

    private void addLineStrip(Coord3f... vertices) {
        FloatBuffer vbuf = getCurrentBuffer();
        try {
            for (int i = 0; i < vertices.length - 1; i++) {
                Coord3f a = vertices[i];
                Coord3f b = vertices[i + 1];
                vbuf.put(a.x).put(a.y).put(a.z);
                vbuf.put(b.x).put(b.y).put(b.z);
            }
        } catch (BufferOverflowException e) { // ignored
        	System.out.println(e);
        }
    }

    private FloatBuffer getCurrentBuffer() {
        return vertexBuffers[curIndex];
    }

    private void swapBuffers() {
        curIndex = (curIndex + 1) % 2;
    }
}
