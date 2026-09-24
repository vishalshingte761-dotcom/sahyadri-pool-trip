package com.sahyadri.sahyadripooltrip.service;

import com.sahyadri.sahyadripooltrip.entity.Fort;
import com.sahyadri.sahyadripooltrip.entity.FortRoute;
import com.sahyadri.sahyadripooltrip.repository.FortRepository;
import com.sahyadri.sahyadripooltrip.repository.FortRouteRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FortCsvImportService {

    private static final String CSV_FILE = "data/MAHARASHTRA_FORTS_MASTER_FINAL_390.csv";

    private static final String EXPECTED_HEADER = "fort_id,fort_name,district,locality,base_village,latitude,longitude,"
            + "location_source,location_confidence,elevation_m,route_id,route_name,"
            + "route_start,route_start_latitude,route_start_longitude,route_end,"
            + "route_distance_km,route_duration,route_difficulty,route_type,"
            + "trek_start,trek_route,trek_distance_km,trek_duration,difficulty,"
            + "best_season,protection_status,route_source,route_verification_status,"
            + "gps_status,trek_status,primary_source,verification_status,"
            + "location_verified_date,last_verified,dataset_scope,record_status,"
            + "base_village_source,trek_start_latitude,trek_start_longitude,"
            + "route_confidence";

    private final FortRepository fortRepository;
    private final FortRouteRepository fortRouteRepository;

    public FortCsvImportService(
            FortRepository fortRepository,
            FortRouteRepository fortRouteRepository) {
        this.fortRepository = fortRepository;
        this.fortRouteRepository = fortRouteRepository;
    }

    @Transactional
    public ImportResult importForts() throws Exception {

        ClassPathResource resource = new ClassPathResource(CSV_FILE);

        if (!resource.exists()) {
            throw new IllegalStateException(
                    "Master CSV not found: " + CSV_FILE);
        }

        int fortCount = 0;
        int routeCount = 0;
        int skippedRouteCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        resource.getInputStream(),
                        StandardCharsets.UTF_8))) {

            String header = reader.readLine();

            if (header == null || header.isBlank()) {
                throw new IllegalStateException(
                        "Master CSV is empty or header is missing.");
            }

            header = removeBom(header).trim();

            List<String> headerColumns = parseCsvLine(header);
            List<String> expectedHeaderColumns = parseCsvLine(EXPECTED_HEADER);

            if (!headerColumns.equals(expectedHeaderColumns)) {
                throw new IllegalStateException(
                        "Master CSV header does not match the expected schema.");
            }

            String line;
            int rowNumber = 1;

            while ((line = reader.readLine()) != null) {

                rowNumber++;

                if (line.isBlank()) {
                    continue;
                }

                List<String> columns = parseCsvLine(line);

                if (columns.size() != 41) {
                    throw new IllegalStateException(
                            "Invalid CSV row at line "
                                    + rowNumber
                                    + ". Expected 41 columns but found "
                                    + columns.size());
                }

                String fortId = value(columns, 0);
                String fortName = value(columns, 1);

                if (fortId == null || fortId.isBlank()) {
                    throw new IllegalStateException(
                            "Missing fort_id at CSV line " + rowNumber);
                }

                if (fortName == null || fortName.isBlank()) {
                    throw new IllegalStateException(
                            "Missing fort_name for fort_id "
                                    + fortId
                                    + " at CSV line "
                                    + rowNumber);
                }

                /*
                 * ---------------------------------------------------------
                 * FORT DATA
                 * ---------------------------------------------------------
                 */

                Fort fort = new Fort();

                fort.setFortId(fortId);
                fort.setFortName(fortName);
                fort.setDistrict(value(columns, 2));
                fort.setLocality(value(columns, 3));
                fort.setBaseVillage(value(columns, 4));

                fort.setLatitude(
                        parseDouble(value(columns, 5)));

                fort.setLongitude(
                        parseDouble(value(columns, 6)));

                fort.setLocationSource(value(columns, 7));
                fort.setLocationConfidence(value(columns, 8));

                fort.setElevationM(
                        parseDouble(value(columns, 9)));

                fort.setBestSeason(value(columns, 25));
                fort.setProtectionStatus(value(columns, 26));

                fort.setPrimarySource(value(columns, 31));
                fort.setVerificationStatus(value(columns, 32));
                fort.setLocationVerifiedDate(value(columns, 33));
                fort.setLastVerified(value(columns, 34));
                fort.setDatasetScope(value(columns, 35));
                fort.setRecordStatus(value(columns, 36));

                fort.setBaseVillageSource(value(columns, 37));

                /*
                 * save() with fort_id as @Id makes the import repeatable.
                 * Existing records with the same fort_id are updated
                 * instead of creating duplicate Fort records.
                 */

                fortRepository.save(fort);
                fortCount++;

                /*
                 * ---------------------------------------------------------
                 * ROUTE DATA
                 * ---------------------------------------------------------
                 *
                 * Route information starts from column 10.
                 *
                 * If route_id is blank, no FortRoute is created.
                 * This prevents fake/empty routes.
                 */

                String routeId = value(columns, 10);

                if (routeId == null || routeId.isBlank()) {
                    skippedRouteCount++;
                    continue;
                }

                FortRoute route = new FortRoute();

                route.setRouteId(routeId);
                route.setFortId(fortId);

                route.setRouteName(value(columns, 11));
                route.setRouteStart(value(columns, 12));

                route.setRouteStartLatitude(
                        parseDouble(value(columns, 13)));

                route.setRouteStartLongitude(
                        parseDouble(value(columns, 14)));

                route.setRouteEnd(value(columns, 15));

                route.setRouteDistanceKm(
                        parseDouble(value(columns, 16)));

                route.setRouteDuration(value(columns, 17));
                route.setRouteDifficulty(value(columns, 18));
                route.setRouteType(value(columns, 19));

                route.setTrekStart(value(columns, 20));
                route.setTrekRoute(value(columns, 21));

                route.setTrekDistanceKm(
                        parseDouble(value(columns, 22)));

                route.setTrekDuration(value(columns, 23));
                route.setDifficulty(value(columns, 24));

                route.setBestSeason(value(columns, 25));
                route.setProtectionStatus(value(columns, 26));

                route.setRouteSource(value(columns, 27));
                route.setRouteVerificationStatus(
                        value(columns, 28));

                route.setGpsStatus(value(columns, 29));
                route.setTrekStatus(value(columns, 30));

                route.setPrimarySource(value(columns, 31));
                route.setVerificationStatus(value(columns, 32));
                route.setLocationVerifiedDate(value(columns, 33));
                route.setLastVerified(value(columns, 34));
                route.setDatasetScope(value(columns, 35));
                route.setRecordStatus(value(columns, 36));

                route.setBaseVillageSource(value(columns, 37));

                route.setTrekStartLatitude(
                        parseDouble(value(columns, 38)));

                route.setTrekStartLongitude(
                        parseDouble(value(columns, 39)));

                route.setRouteConfidence(value(columns, 40));

                /*
                 * Same route_id will update the existing route
                 * instead of creating a duplicate.
                 */

                fortRouteRepository.save(route);
                routeCount++;
            }
        }

        return new ImportResult(
                fortCount,
                routeCount,
                skippedRouteCount);
    }

    private String value(
            List<String> columns,
            int index) {

        if (index >= columns.size()) {
            return null;
        }

        String value = columns.get(index);

        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isEmpty()) {
            return null;
        }

        return value;
    }

    private Double parseDouble(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim();

        // Approximate numeric values such as "~7" or "~6.5"
        // are accepted by removing only the leading "~".
        if (normalized.startsWith("~")) {
            normalized = normalized.substring(1).trim();
        }

        try {
            return Double.parseDouble(normalized);

        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Invalid numeric value in CSV: "
                            + value,
                    exception);
        }
    }

    private String removeBom(String value) {

        if (value != null && !value.isEmpty()
                && value.charAt(0) == '\uFEFF') {

            return value.substring(1);
        }

        return value;
    }

    private List<String> parseCsvLine(String line) {

        List<String> columns = new ArrayList<>();

        StringBuilder current = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char character = line.charAt(i);

            if (character == '"') {

                if (insideQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    current.append('"');
                    i++;

                } else {

                    insideQuotes = !insideQuotes;
                }

            } else if (character == ','
                    && !insideQuotes) {

                columns.add(current.toString());
                current.setLength(0);

            } else {

                current.append(character);
            }
        }

        columns.add(current.toString());

        return columns;
    }

    public record ImportResult(
            int fortCount,
            int routeCount,
            int skippedRouteCount) {
    }
}