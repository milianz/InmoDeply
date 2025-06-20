package org.milianz.inmomarketbackend.Services;


import org.milianz.inmomarketbackend.Domain.Entities.DTOs.PublicationSaveDTO;
import org.milianz.inmomarketbackend.Domain.Entities.Location;
import org.milianz.inmomarketbackend.Domain.Repositories.iLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private iLocationRepository locationRepository;

    public Location createLocation(PublicationSaveDTO publicationSaveDTO) {

        Optional<Location> existingLocation = locationRepository.findByNeighborhoodAndMunicipalityAndDepartment(publicationSaveDTO.getNeighborhood(),
                publicationSaveDTO.getMunicipality(), publicationSaveDTO.getDepartment());

        if (existingLocation.isPresent()) {
            return existingLocation.get();
        }

        Location location = new Location();
        location.setNeighborhood(publicationSaveDTO.getNeighborhood());
        location.setMunicipality(publicationSaveDTO.getMunicipality());
        location.setDepartment(publicationSaveDTO.getDepartment());
        location = locationRepository.save(location);

        return location;
    }
}
