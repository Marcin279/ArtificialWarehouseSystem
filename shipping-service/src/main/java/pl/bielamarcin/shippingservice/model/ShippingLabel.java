package pl.bielamarcin.shippingservice.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class ShippingLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID shipmentId;

    @Column(nullable = false)
    private String labelUrl; // link do etykiety PDF

    // Gettery i settery

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getShipmentId() { return shipmentId; }
    public void setShipmentId(UUID shipmentId) { this.shipmentId = shipmentId; }

    public String getLabelUrl() { return labelUrl; }
    public void setLabelUrl(String labelUrl) { this.labelUrl = labelUrl; }
}
