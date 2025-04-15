package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import androidx.core.content.ContextCompat;
import org.mapsforge.core.graphics.*;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.datastore.MultiMapDataStore;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MapViewManager {
    private final Activity mActivity;
    private final MapView mMapView;
    private final GraphicFactory mGraphicFactory;
    private final List<TileRendererLayer> mLayers;
    private MultiMapDataStore mMultiMapDataStore;
    private TileRendererLayer mTileRendererLayer;
    private TileCache mTileCache;
    public MapViewManager(MainActivity activity, MapView mapView) {
        this.mActivity = activity;
        this.mMapView = mapView;
        this.mGraphicFactory = AndroidGraphicFactory.INSTANCE;
        this.mLayers = new ArrayList<>();
        mMultiMapDataStore = new MultiMapDataStore(MultiMapDataStore.DataPolicy.DEDUPLICATE); // Check if problems
        mTileCache = AndroidUtil.createTileCache(
                mActivity,
                "multi_tilecache",
                mapView.getModel().displayModel.getTileSize(),
                1.0f,
                mapView.getModel().frameBufferModel.getOverdrawFactor()
        );
        try {
            mMapView.getMapScaleBar().setVisible(true);
            mMapView.setBuiltInZoomControls(true);

        } catch (Exception e) {
            Log.e("MapViewManager", "Error al inicializar el objeto", e);
        }
    }

    public void loadMap(Uri uri){
        Log.d("MapViewManager", "URI: " + uri.toString());
        try {
            FileInputStream fis = (FileInputStream) mActivity.getContentResolver().openInputStream(uri);

            MapDataStore newMap = new MapFile(fis);
//            boolean isFirstElement = mLayers.isEmpty();
//            mMultiMapDataStore.addMapDataStore(newMap,!isFirstElement,!isFirstElement);
            mMultiMapDataStore.addMapDataStore(newMap,false,false);
            if (mTileRendererLayer != null) {
                mMapView.getLayerManager().getLayers().remove(mTileRendererLayer);
            }
            mTileRendererLayer = new TileRendererLayer(
                    mTileCache,
                    mMultiMapDataStore,
                    mMapView.getModel().mapViewPosition,
                    AndroidGraphicFactory.INSTANCE);

            mTileRendererLayer.setXmlRenderTheme(MapsforgeThemes.DEFAULT);

            /*
             * On its own a tileRendererLayer does not know where to display the map, so we need to
             * associate it with our mapView.
             */
            mMapView.getLayerManager().getLayers().add(mTileRendererLayer);
//            mLayers.add(mTileRendererLayer);
            Log.i("MapViewManager", "Capa añadida correctamente desde URI: " + uri.toString());

//            /*
//             * The map also needs to know which area to display and at what zoom level.
//             * Note: this map position is specific to El Teide area.
//             */
//            mMapView.setCenter(new LatLong(28.272440, -16.642372));
//            mMapView.setZoomLevel((byte) 8);
//
//            LatLong simulatedPosition = new LatLong(30.675, -14.602);
//
//            Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
//            paintStroke.setColor(Color.BLACK);
//            paintStroke.setStrokeWidth(5);
//            paintStroke.setStyle(Style.STROKE);
//            paintStroke.setDashPathEffect(new float[]{20, 20}); // Línea segmentada
//
//            // Crear la lista de coordenadas
//            List<LatLong> geoPoints = new ArrayList<>();
//            geoPoints.add(simulatedPosition);
//            geoPoints.add(new LatLong(28.4636, -16.2518)); // Santa Cruz de Tenerife
//
//            // Crear la Polyline
//            Polyline polyline = new Polyline(paintStroke, AndroidGraphicFactory.INSTANCE);
//            polyline.setPoints(geoPoints);
//
//            // Agregar la línea a las capas del mapa
//            layers.add(polyline);
//
//            Paint paintStroke2 = AndroidGraphicFactory.INSTANCE.createPaint();
//            paintStroke2.setColor(Color.RED);
//            paintStroke2.setStrokeWidth(4);
//            paintStroke2.setStyle(Style.STROKE);
//
//            // Crear la lista de coordenadas
//            List<LatLong> geoPoints2 = new ArrayList<>();
//            geoPoints2.add(new LatLong(40.472222, -3.560833)); // Las Palmas de Gran Canaria
//            geoPoints2.add(simulatedPosition); // TFS
//
//            // Crear la Polyline
//            Polyline polyline2 = new Polyline(paintStroke2, AndroidGraphicFactory.INSTANCE);
//            polyline2.setPoints(geoPoints2);
//            layers.add(polyline2);
//
//            // Cargar el icono desde los recursos con ContextCompat
//            Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.airplane);
//
//            // Verificar que el drawable no sea nulo
//            if (drawable == null) {
//                return;
//            }
//
//            // Convertir el Drawable a Bitmap
//            Bitmap iconBitmap = AndroidGraphicFactory.convertToBitmap(drawable);
//            // Crear el marcador
//            Marker marker = new Marker(simulatedPosition, iconBitmap, 0, 0);
//
//            // Agregar el marcador al mapa
//            layers.add(marker);

        } catch (Exception e) {
            Log.e("MapViewManager", "Error al añadir mapa desde URI", e);
        }
    }
}

