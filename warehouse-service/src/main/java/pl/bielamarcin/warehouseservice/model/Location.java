package pl.bielamarcin.warehouseservice.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "locations")
public class Location {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String shelf;

    @Column(nullable = false)
    private String bin;

    @Column(nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "location")
    private Set<InventoryItem> items;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Set<InventoryItem> getItems() {
        return items;
    }

    public void setItems(Set<InventoryItem> items) {
        this.items = items;
    }
}