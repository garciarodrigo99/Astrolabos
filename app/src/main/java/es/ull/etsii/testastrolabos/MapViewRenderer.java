package es.ull.etsii.testastrolabos;

import android.util.Log;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes;

import java.util.ArrayList;
import java.util.List;

public class MapViewRenderer {
    private final MapViewManager mMapViewManager;
    private final GraphicFactory mGraphicFactory;
    private final List<Layer> mLayers;
    private MultiMapDataStore mMultiMapDataStore;
    private TileRendererLayer mTileRendererLayer;
    private TileCache mTileCache;

    public MapViewRenderer(MapViewManager mapViewManager) {
        this.mMapViewManager = mapViewManager;
        this.mGraphicFactory = AndroidGraphicFactory.INSTANCE;
        this.mLayers = new ArrayList<>();
        try {
            mMultiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.RETURN_ALL); // Check if problems
            mTileCache = AndroidUtil.createTileCache(
                    mMapViewManager.mActivity,
                    "multi_tilecache",
                    mMapViewManager.mMapView.getModel().displayModel.getTileSize(),
                    1.0f,
                    mMapViewManager.mMapView.getModel().frameBufferModel.getOverdrawFactor()
            );
        } catch (Exception e) {
            Log.e("MapViewRenderer", "Error creating renderer", e);
        }
    }

    public GraphicFactory getGraphicFactory() {
        return mGraphicFactory;
    }

    public List<Layer> getLayers() {
        return mLayers;
    }

    public MultiMapDataStore getMultiMapDataStore() {
        return mMultiMapDataStore;
    }

    public TileRendererLayer getTileRendererLayer() {
        return mTileRendererLayer;
    }

    public void initTileRendererLayer() {
        mTileRendererLayer = new TileRendererLayer(
                mTileCache,
                mMultiMapDataStore,
                mMapViewManager.mMapView.getModel().mapViewPosition,
                mGraphicFactory);
        mTileRendererLayer.setXmlRenderTheme(MapsforgeThemes.DEFAULT);
    }

    public TileCache getTileCache() {
        return mTileCache;
    }
}
