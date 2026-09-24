package com.sahyadri.sahyadripooltrip.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Keeps the existing trips table compatible with agency departures.
 * Older database versions created driver_id as NOT NULL because every trip
 * originally belonged to a driver. Agency departures intentionally use
 * agency_id + agency_vehicle_id instead, so driver_id must allow NULL.
 */
@Component
@org.springframework.context.annotation.DependsOn("entityManagerFactory")
public class AgencyTripSchemaCompatibility {

    private static final Logger log = LoggerFactory.getLogger(AgencyTripSchemaCompatibility.class);
    private final DataSource dataSource;

    public AgencyTripSchemaCompatibility(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @jakarta.annotation.PostConstruct
    public void ensureAgencyTripSchema() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS "
                             + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'trips' "
                             + "AND COLUMN_NAME = 'driver_id'")) {

            String columnType = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    columnType = rs.getString(1);
                }
            }

            if (columnType == null || columnType.isBlank()) {
                log.debug("trips.driver_id was not found; Hibernate schema update will handle the table.");
                return;
            }

            try (var alter = connection.createStatement()) {
                alter.executeUpdate("ALTER TABLE trips MODIFY COLUMN driver_id " + columnType + " NULL");
            }
            log.debug("Ensured trips.driver_id allows NULL for agency departures.");
        } catch (Exception ex) {
            // This compatibility adjustment must never stop the application.
            // Hibernate remains responsible for normal schema creation/update.
            log.warn("Could not apply agency trip schema compatibility update: {}", ex.getMessage());
        }
    }
}
