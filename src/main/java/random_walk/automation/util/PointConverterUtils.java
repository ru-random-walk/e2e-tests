package random_walk.automation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.Arrays;
import java.util.List;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointConverterUtils {

    @Step
    @Title("AND: Преобразуем строку из базы данных в объект Point")
    public static org.locationtech.jts.geom.Point convertToPoint(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            byte[] wkbBytes = hexStringToByteArray(dbData);
            var geometryFactory = new GeometryFactory();
            WKBReader reader = new WKBReader(geometryFactory);
            var geometry = reader.read(wkbBytes);
            return geometry.getCentroid();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Point format: " + dbData, e);
        }
    }

    @Step
    @Title("AND: Получаем список координатов из строки в базе данных")
    public static List<Double> getPointCoordinatesFromString(String point) {
        return Arrays.stream(point.replace("POINT (", "").replace(")", "").split(" ")).map(Double::parseDouble).toList();
    }
}
