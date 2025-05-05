package random_walk.automation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointConverterUtils {
    public static org.locationtech.jts.geom.Point convertToPoint(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        try {
            // Преобразуем hex в байтовый массив
            byte[] wkbBytes = hexStringToByteArray(dbData);

            // Создаем объекты для чтения
            var geometryFactory = new GeometryFactory();
            WKBReader reader = new WKBReader(geometryFactory);
            // Читаем геометрию
            var geometry = reader.read(wkbBytes);
            return geometry.getCentroid();
        }

        catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Point format: " + dbData, e);
        }
    }
}
