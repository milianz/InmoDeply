package org.milianz.inmomarketbackend.Services;

import org.milianz.inmomarketbackend.Domain.Entities.PropertyImage;
import org.milianz.inmomarketbackend.Domain.Repositories.iPropertyImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PropertyImageService {

    @Autowired
    private iPropertyImageRepository propertyImageRepository;

    public PropertyImage createPropertyImage(String imageUrl) {
        PropertyImage savedPropertyImage = new PropertyImage();
        savedPropertyImage.setImageUrl(imageUrl);

        return savedPropertyImage;
    }

}
