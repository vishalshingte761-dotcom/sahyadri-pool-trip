package com.sahyadri.sahyadripooltrip.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sahyadri.sahyadripooltrip.entity.Agency;
import com.sahyadri.sahyadripooltrip.entity.DriverProfile;
import com.sahyadri.sahyadripooltrip.entity.Role;
import com.sahyadri.sahyadripooltrip.entity.User;
import com.sahyadri.sahyadripooltrip.repository.AgencyRepository;
import com.sahyadri.sahyadripooltrip.repository.DriverProfileRepository;
import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.service.FileStorageService;
import com.sahyadri.sahyadripooltrip.service.InputValidationService;
import com.sahyadri.sahyadripooltrip.service.VehicleOptionsService;

@RestController
@RequestMapping("/api/partners")
public class PartnerRegistrationController {

    private final UserRepository users;
    private final DriverProfileRepository drivers;
    private final AgencyRepository agencies;
    private final BCryptPasswordEncoder encoder;
    private final FileStorageService files;
    private final InputValidationService validation;
    private final VehicleOptionsService vehicleOptionsService;

    public PartnerRegistrationController(
            UserRepository users,
            DriverProfileRepository drivers,
            AgencyRepository agencies,
            BCryptPasswordEncoder encoder,
            FileStorageService files,
            InputValidationService validation,
            VehicleOptionsService vehicleOptionsService) {

        this.users = users;
        this.drivers = drivers;
        this.agencies = agencies;
        this.encoder = encoder;
        this.files = files;
        this.validation = validation;
        this.vehicleOptionsService = vehicleOptionsService;
    }

    private String req(String value, String name) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }

        return value.trim();
    }

    private String requiredFile(
            MultipartFile file,
            String name,
            long maxBytes) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(name + " is required");
        }

        return files.store(
                file,
                "documents",
                maxBytes,
                false);
    }

    private String optionalFile(
            MultipartFile file,
            String folder,
            long maxBytes) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        return files.store(
                file,
                folder,
                maxBytes,
                false);
    }

    private String trackedRequiredFile(
            MultipartFile file,
            String name,
            long maxBytes,
            List<String> uploadedFiles) {

        String path = requiredFile(file, name, maxBytes);
        uploadedFiles.add(path);
        return path;
    }

    private String trackedOptionalFile(
            MultipartFile file,
            String folder,
            long maxBytes,
            List<String> uploadedFiles) {

        String path = optionalFile(file, folder, maxBytes);

        if (path != null) {
            uploadedFiles.add(path);
        }

        return path;
    }

    private void cleanupUploadedFiles(List<String> uploadedFiles) {

        for (String path : uploadedFiles) {

            try {
                files.delete(path);
            } catch (Exception cleanupException) {

                // Original registration error must not be hidden.
                System.err.println(
                        "File cleanup failed for: " + path);
            }
        }
    }

    // =========================================================
    // DRIVER REGISTRATION
    // =========================================================

    @Transactional
    @PostMapping(
            value = "/driver/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerDriver(

            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,

            @RequestParam(required = false)
            String dateOfBirth,

            @RequestParam(required = false)
            String address,

            @RequestParam String emergencyContactName,
            @RequestParam String emergencyContactNumber,
            @RequestParam String emergencyContactRelation,

            @RequestParam String vehicleRegistrationNumber,
            @RequestParam String vehicleType,
            @RequestParam Integer seatingCapacity,

            @RequestPart("aadhaarDocument")
            MultipartFile aadhaarDocument,

            @RequestPart("drivingLicenceDocument")
            MultipartFile drivingLicenceDocument,

            @RequestPart("rcDocument")
            MultipartFile rcDocument,

            @RequestPart("insuranceDocument")
            MultipartFile insuranceDocument,

            @RequestPart("pucDocument")
            MultipartFile pucDocument,

            @RequestPart(
                    value = "panDocument",
                    required = false)
            MultipartFile panDocument,

            @RequestPart(
                    value = "addressProofDocument",
                    required = false)
            MultipartFile addressProofDocument,

            @RequestPart(
                    value = "vehiclePhoto",
                    required = false)
            MultipartFile vehiclePhoto,

            @RequestPart(
                    value = "commercialPermitDocument",
                    required = false)
            MultipartFile commercialPermitDocument,

            @RequestPart(
                    value = "fitnessCertificateDocument",
                    required = false)
            MultipartFile fitnessCertificateDocument) {

        List<String> uploadedFiles = new ArrayList<>();

        try {

            // =================================================
            // BASIC VALIDATION
            // =================================================

            fullName = req(fullName, "Full name");

            email = validation.email(email);

            phone = validation.phone(phone, true);

            password = req(password, "Password");

            emergencyContactName =
                    req(
                            emergencyContactName,
                            "Emergency contact name");

            emergencyContactNumber =
                    validation.phone(
                            emergencyContactNumber,
                            true);

            emergencyContactRelation =
                    req(
                            emergencyContactRelation,
                            "Emergency contact relation");

            vehicleRegistrationNumber =
                    req(
                            vehicleRegistrationNumber,
                            "Vehicle registration number");

            vehicleType =
                    req(
                            vehicleType,
                            "Vehicle type");

            // =================================================
            // DUPLICATE USER CHECK
            // =================================================

            if (users.existsByEmail(email)) {

                throw new IllegalArgumentException(
                        "Email is already registered");
            }

            if (users.existsByPhone(phone)) {

                throw new IllegalArgumentException(
                        "Phone is already registered");
            }

            // =================================================
            // VEHICLE + SEAT VALIDATION
            // =================================================

            vehicleOptionsService.validate(
                    vehicleType,
                    seatingCapacity);

            // =================================================
            // USER CREATION
            // =================================================

            User user = new User();

            user.setName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPasswordHash(
                    encoder.encode(password));
            user.setRole(Role.DRIVER);

            user = users.save(user);

            // =================================================
            // DRIVER PROFILE
            // =================================================

            DriverProfile profile =
                    new DriverProfile();

            profile.setUserId(
                    user.getUserId());

            profile.setFullName(fullName);

            profile.setDateOfBirth(
                    dateOfBirth == null
                            || dateOfBirth.isBlank()
                            ? null
                            : LocalDate.parse(
                                    dateOfBirth.trim()));

            profile.setAddress(address);

            profile.setEmergencyContactName(
                    emergencyContactName);

            profile.setEmergencyContactNumber(
                    emergencyContactNumber);

            profile.setEmergencyContactRelation(
                    emergencyContactRelation);

            profile.setVehicleRegistrationNumber(
                    vehicleRegistrationNumber);

            profile.setVehicleType(
                    vehicleType);

            profile.setSeatingCapacity(
                    seatingCapacity);

            // =================================================
            // REQUIRED DOCUMENTS
            // =================================================

            profile.setAadhaarDocument(
                    trackedRequiredFile(
                            aadhaarDocument,
                            "Aadhaar document",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setDrivingLicenceDocument(
                    trackedRequiredFile(
                            drivingLicenceDocument,
                            "Driving Licence document",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setRcDocument(
                    trackedRequiredFile(
                            rcDocument,
                            "RC document",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setInsuranceDocument(
                    trackedRequiredFile(
                            insuranceDocument,
                            "Insurance document",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setPucDocument(
                    trackedRequiredFile(
                            pucDocument,
                            "PUC document",
                            10 * 1024 * 1024,
                            uploadedFiles));

            // =================================================
            // OPTIONAL DOCUMENTS
            // =================================================

            profile.setPanDocument(
                    trackedOptionalFile(
                            panDocument,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setAddressProofDocument(
                    trackedOptionalFile(
                            addressProofDocument,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setVehiclePhoto(
                    trackedOptionalFile(
                            vehiclePhoto,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setCommercialPermitDocument(
                    trackedOptionalFile(
                            commercialPermitDocument,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            profile.setFitnessCertificateDocument(
                    trackedOptionalFile(
                            fitnessCertificateDocument,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            // =================================================
            // SAVE DRIVER PROFILE
            // =================================================

            drivers.save(profile);

            // =================================================
            // SUCCESS
            // =================================================

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            java.util.Map.of(
                                    "success",
                                    true,

                                    "message",
                                    "Driver registration submitted. Admin verification is required before login/trip publishing.",

                                    "userId",
                                    user.getUserId(),

                                    "verificationStatus",
                                    profile
                                            .getVerificationStatus()
                                            .name()));

        } catch (RuntimeException ex) {

            // =================================================
            // DATABASE ROLLBACK
            // =================================================
            // @Transactional handles DB rollback.

            // =================================================
            // FILESYSTEM CLEANUP
            // =================================================

            cleanupUploadedFiles(
                    uploadedFiles);

            throw ex;
        }
    }

    // =========================================================
    // AGENCY REGISTRATION
    // =========================================================

    @Transactional
    @PostMapping(
            value = "/agency/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerAgency(

            @RequestParam String businessName,
            @RequestParam String ownerName,
            @RequestParam String mobile,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String businessAddress,
            @RequestParam String cityDistrict,

            @RequestParam(required = false)
            String agencyType,

            @RequestParam(required = false)
            Integer numberOfVehicles,

            @RequestParam(required = false)
            Integer numberOfDrivers,

            @RequestPart("aadhaarDocument")
            MultipartFile aadhaarDocument,

            @RequestPart("panUdyamDocument")
            MultipartFile panUdyamDocument,

            @RequestPart("bankPassbookDocument")
            MultipartFile bankPassbookDocument,

            @RequestPart("ownerIdDocument")
            MultipartFile ownerIdDocument,

            @RequestPart("shopActDocument")
            MultipartFile shopActDocument,

            @RequestPart(
                    value = "gstCertificate",
                    required = false)
            MultipartFile gstCertificate,

            @RequestPart(
                    value = "mtdcTourismCertificate",
                    required = false)
            MultipartFile mtdcTourismCertificate,

            @RequestPart(
                    value = "partnershipOrIncorporationCertificate",
                    required = false)
            MultipartFile partnershipOrIncorporationCertificate,

            @RequestPart(
                    value = "commercialVehicleDocuments",
                    required = false)
            MultipartFile commercialVehicleDocuments,

            @RequestPart(
                    value = "iataIrctcLicense",
                    required = false)
            MultipartFile iataIrctcLicense,

            @RequestPart(
                    value = "agencyLogo",
                    required = false)
            MultipartFile agencyLogo) {

        List<String> uploadedFiles = new ArrayList<>();

        try {

            // =================================================
            // BASIC VALIDATION
            // =================================================

            businessName =
                    req(
                            businessName,
                            "Business name");

            ownerName =
                    req(
                            ownerName,
                            "Owner name");

            mobile =
                    validation.phone(
                            mobile,
                            true);

            email =
                    validation.email(
                            email);

            password =
                    req(
                            password,
                            "Password");

            businessAddress =
                    req(
                            businessAddress,
                            "Business address");

            cityDistrict =
                    req(
                            cityDistrict,
                            "City/District");

            // =================================================
            // DUPLICATE USER CHECK
            // =================================================

            if (users.existsByEmail(email)) {

                throw new IllegalArgumentException(
                        "Email is already registered");
            }

            if (users.existsByPhone(mobile)) {

                throw new IllegalArgumentException(
                        "Mobile is already registered");
            }

            // =================================================
            // USER CREATION
            // =================================================

            User user = new User();

            user.setName(ownerName);
            user.setEmail(email);
            user.setPhone(mobile);
            user.setPasswordHash(
                    encoder.encode(password));
            user.setRole(Role.AGENCY);

            user = users.save(user);

            // =================================================
            // AGENCY PROFILE
            // =================================================

            Agency agency =
                    new Agency();

            agency.setOwnerUserId(
                    user.getUserId());

            agency.setBusinessName(
                    businessName);

            agency.setOwnerName(
                    ownerName);

            agency.setMobile(
                    mobile);

            agency.setEmail(
                    email);

            agency.setBusinessAddress(
                    businessAddress);

            agency.setCityDistrict(
                    cityDistrict);

            agency.setAgencyType(
                    agencyType);

            agency.setNumberOfVehicles(
                    numberOfVehicles);

            agency.setNumberOfDrivers(
                    numberOfDrivers);

            // =================================================
            // REQUIRED DOCUMENTS
            // =================================================

            agency.setAadhaarDocument(
                    trackedRequiredFile(
                            aadhaarDocument,
                            "Aadhaar document",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setPanUdyamDocument(
                    trackedRequiredFile(
                            panUdyamDocument,
                            "PAN/Udyam document",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setBankPassbookDocument(
                    trackedRequiredFile(
                            bankPassbookDocument,
                            "Bank passbook",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setOwnerIdDocument(
                    trackedRequiredFile(
                            ownerIdDocument,
                            "Owner ID proof",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setShopActDocument(
                    trackedRequiredFile(
                            shopActDocument,
                            "Shop Act License",
                            10 * 1024 * 1024,
                            uploadedFiles));

            // =================================================
            // OPTIONAL DOCUMENTS
            // =================================================

            agency.setGstCertificate(
                    trackedOptionalFile(
                            gstCertificate,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setMtdcTourismCertificate(
                    trackedOptionalFile(
                            mtdcTourismCertificate,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setPartnershipOrIncorporationCertificate(
                    trackedOptionalFile(
                            partnershipOrIncorporationCertificate,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setCommercialVehicleDocuments(
                    trackedOptionalFile(
                            commercialVehicleDocuments,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setIataIrctcLicense(
                    trackedOptionalFile(
                            iataIrctcLicense,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            agency.setAgencyLogo(
                    trackedOptionalFile(
                            agencyLogo,
                            "documents",
                            10 * 1024 * 1024,
                            uploadedFiles));

            // =================================================
            // SAVE AGENCY PROFILE
            // =================================================

            agencies.save(agency);

            // =================================================
            // SUCCESS
            // =================================================

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            java.util.Map.of(
                                    "success",
                                    true,

                                    "message",
                                    "Agency registration submitted. Admin document verification is required.",

                                    "userId",
                                    user.getUserId(),

                                    "verificationStatus",
                                    agency
                                            .getVerificationStatus()
                                            .name()));

        } catch (RuntimeException ex) {

            // =================================================
            // DATABASE ROLLBACK
            // =================================================
            // @Transactional handles DB rollback.

            // =================================================
            // FILESYSTEM CLEANUP
            // =================================================

            cleanupUploadedFiles(
                    uploadedFiles);

            throw ex;
        }
    }
}