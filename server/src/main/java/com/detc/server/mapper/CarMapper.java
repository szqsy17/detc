package com.detc.server.mapper;

import com.detc.server.model.CarModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author szqsy17
 */
@Repository
public interface CarMapper {

    @Insert("INSERT INTO car (vin, version, iviVersion, clusterVersion, mcuVersion)" +
            "VALUES (#{vin}, #{version}, #{iviVersion}, #{clusterVersion}, #{mcuVersion})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addOneCar(CarModel carModel);

    @UpdateProvider(type = CarSqlProvider.class, method = "toggleCarUpgrade")
    int toggleCarUpgrade(String id, Integer before);

    @Update("UPDATE car SET targetVersionId = #{targetVersionId} WHERE id = #{id}")
    int changeCarTargetVersion(String id, String targetVersionId);

    @Update("UPDATE car SET targetVersionId = '' WHERE targetVersionId = #{targetVersionId}")
    void resetCarTargetVersion(String targetVersionId);

    @Update("UPDATE car SET version = #{version}, iviVersion = #{iviVersion}, clusterVersion = #{clusterVersion}, mcuVersion = #{mcuVersion}, targetVersionId = #{targetVersionId} WHERE vin = #{vin}")
    void carUpgradeSuccess(CarModel carModel);

    @Select("SELECT * FROM car WHERE vin = #{vin}")
    CarModel getOneCar(String vin);

    @Select("SELECT car.*, mirror.mirrorVersion AS targetVersion FROM car LEFT JOIN mirror ON car.targetVersionId = mirror.id ORDER BY car.id DESC")
    List<Map<String, Object>> getAllCar();


}
