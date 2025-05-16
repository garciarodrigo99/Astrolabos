package es.ull.etsii.testastrolabos.DAO.AirportDAO;

import es.ull.etsii.testastrolabos.DataModels.Airport;

import java.util.List;

public interface AirportDAO {
    void add(Airport airport);
    void update(Airport airport);
    void delete(Airport airport);
    List<Airport> getAllAirports();
    Airport getAirportByIATACode(String code);
    Airport getAirportByICAOCode(String code);
}
