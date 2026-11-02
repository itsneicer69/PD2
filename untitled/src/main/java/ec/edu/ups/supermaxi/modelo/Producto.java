package ec.edu.ups.supermaxi.modelo;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "productos")
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_barras", nullable = false, unique = true, length = 30)
    private String codigoBarras;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "precio_unitario", nullable = false)
    private double precioUnitario;

    @Column(name = "stock_actual", nullable = false)
    private int stockActual;

    // Relación N:1 con Proveedor (Muchos productos son suministrados por un proveedor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    public Producto() {
    }

    public Producto(String codigoBarras, String nombre, double precioUnitario, int stockActual, Proveedor proveedor) {
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.stockActual = stockActual;
        this.proveedor = proveedor;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precioUnitario=" + precioUnitario +
                ", stockActual=" + stockActual +
                '}';
    }
}