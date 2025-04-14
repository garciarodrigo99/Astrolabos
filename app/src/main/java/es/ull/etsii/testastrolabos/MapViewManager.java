package es.ull.etsii.testastrolabos;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
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
    private Activity mActivity;
    private MapView mMapView;

    public MapViewManager(MainActivity activity, MapView mapView) {
        this.mActivity = activity;
        this.mMapView = mapView;
    }

    public void loadMap(Uri uri){
        try {
            /*
             * We then make some simple adjustments, such as showing a scale bar and zoom controls.
             */
            mMapView.getMapScaleBar().setVisible(true);
            mMapView.setBuiltInZoomControls(true);

            /*
             * To avoid redrawing all the tiles all the time, we need to set up a tile cache with a
             * utility method.
             */
            TileCache tileCache = AndroidUtil.createTileCache(mActivity, "mapcache",
                    mMapView.getModel().displayModel.getTileSize(), 1f,
                    mMapView.getModel().frameBufferModel.getOverdrawFactor());

            /*
             * Now we need to set up the process of displaying a map. A map can have several layers,
             * stacked on top of each other. A layer can be a map or some visual elements, such as
             * markers. Here we only show a map based on a mapsforge map file. For this we need a
             * TileRendererLayer. A TileRendererLayer needs a TileCache to hold the generated map
             * tiles, a map file from which the tiles are generated and Rendertheme that defines the
             * appearance of the map.
             */
            FileInputStream fis = (FileInputStream) mActivity.getContentResolver().openInputStream(uri);
            MapDataStore mapDataStore = new MapFile(fis);
            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    mMapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(MapsforgeThemes.MOTORIDER);

            /*
             * On its own a tileRendererLayer does not know where to display the map, so we need to
             * associate it with our mapView.
             */
            mMapView.getLayerManager().getLayers().add(tileRendererLayer);

            /*
             * The map also needs to know which area to display and at what zoom level.
             * Note: this map position is specific to El Teide area.
             */
            mMapView.setCenter(new LatLong(28.272440, -16.642372));
            mMapView.setZoomLevel((byte) 8);

            LatLong simulatedPosition = new LatLong(30.675, -14.602);

            Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
            paintStroke.setColor(Color.BLACK);
            paintStroke.setStrokeWidth(5);
            paintStroke.setStyle(Style.STROKE);
            paintStroke.setDashPathEffect(new float[]{20, 20}); // Línea segmentada

            // Crear la lista de coordenadas
            List<LatLong> geoPoints = new ArrayList<>();
            geoPoints.add(simulatedPosition);
            geoPoints.add(new LatLong(28.4636, -16.2518)); // Santa Cruz de Tenerife

            // Crear la Polyline
            Polyline polyline = new Polyline(paintStroke, AndroidGraphicFactory.INSTANCE);
            polyline.setPoints(geoPoints);

            // Agregar la línea a las capas del mapa
            Layers layers = mMapView.getLayerManager().getLayers();
            layers.add(polyline);

            Paint paintStroke2 = AndroidGraphicFactory.INSTANCE.createPaint();
            paintStroke2.setColor(Color.RED);
            paintStroke2.setStrokeWidth(4);
            paintStroke2.setStyle(Style.STROKE);

            // Crear la lista de coordenadas
            List<LatLong> geoPoints2 = new ArrayList<>();
            geoPoints2.add(new LatLong(40.472222, -3.560833)); // Las Palmas de Gran Canaria
            geoPoints2.add(simulatedPosition); // TFS

            // Crear la Polyline
            Polyline polyline2 = new Polyline(paintStroke2, AndroidGraphicFactory.INSTANCE);
            polyline2.setPoints(geoPoints2);
            layers.add(polyline2);

            // Cargar el icono desde los recursos con ContextCompat
            Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.airplane);

            // Verificar que el drawable no sea nulo
            if (drawable == null) {
                return;
            }

            // Convertir el Drawable a Bitmap
            Bitmap iconBitmap = AndroidGraphicFactory.convertToBitmap(drawable);
            // Crear el marcador
            Marker marker = new Marker(simulatedPosition, iconBitmap, 0, 0);

            // Agregar el marcador al mapa
            layers.add(marker);

        } catch (Exception e) {
            /*
             * In case of map file errors avoid crash, but developers should handle these cases!
             */
            e.printStackTrace();
        }
    }
}

