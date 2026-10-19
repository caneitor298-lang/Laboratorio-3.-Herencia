import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {
    private final Scanner entrada;
    private final TecniHogar sistema;
    public Main() { entrada = new Scanner(System.in, "UTF-8"); sistema = new TecniHogar(true); }
    public static void main(String[] args) { new Main().ejecutar(); }
    public void ejecutar() {
        System.out.println("TECNIHOGAR - Servicios tecnicos a domicilio");
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1. Cotizar  2. Registrar orden  3. Registrar cuadrilla  4. Crear jornada"
                + "\n5. Asignar orden  6. Retirar orden  7. Iniciar jornada  8. Registrar resultado"
                + "\n9. Consultar ordenes  10. Consultar cuadrillas  11. Consultar jornadas"
                + "\n12. Reporte de ordenes  13. Reporte de ingresos  14. Jornadas sin cerrar  0. Salir");
            try {
                int opcion = entero("Opcion: ");
                switch (opcion) {
                    case 1: System.out.println(sistema.cotizar(leerOrden())); break;
                    case 2:
                        OrdenServicio orden = leerOrden();
                        System.out.println(sistema.cotizar(orden));
                        if (siNo("Acepta registrar y cobrar el monto completo? (s/n): ")) {
                            sistema.registrarOrden(orden);
                            System.out.println("Orden registrada y cobrada.");
                        } else System.out.println("Cotizacion finalizada sin registro ni cobro.");
                        break;
                    case 3:
                        sistema.registrarCuadrilla(new Cuadrilla(texto("Codigo: "), decimal("Capacidad en horas: "),
                                siNo("Certificada en refrigeracion? (s/n): ")));
                        System.out.println("Cuadrilla registrada."); break;
                    case 4: sistema.crearJornada(texto("Codigo de jornada: "), texto("Codigo de cuadrilla: "));
                        System.out.println("Jornada creada."); break;
                    case 5: sistema.asignar(texto("Jornada: "), texto("Orden: "));
                        System.out.println("Orden asignada."); break;
                    case 6: sistema.retirar(texto("Jornada: "), texto("Orden: "));
                        System.out.println("Orden retirada, pendiente y sin cargo."); break;
                    case 7: sistema.iniciar(texto("Jornada: "));
                        System.out.println("Jornada iniciada. Visitas y cargos adicionales registrados."); break;
                    case 8:
                        String jornada = texto("Jornada: "); String codigo = texto("Orden: ");
                        int resultado = entero("Resultado (1=completada, 2=visita fallida): ");
                        if (resultado != 1 && resultado != 2) throw new IllegalArgumentException("Resultado invalido.");
                        sistema.registrarResultado(jornada, codigo, resultado == 1);
                        System.out.println("Resultado registrado."); break;
                    case 9: for (OrdenServicio o : sistema.getOrdenes()) System.out.println(o); break;
                    case 10: for (Cuadrilla c : sistema.getCuadrillas()) System.out.println(c); break;
                    case 11:
                        for (Jornada j : sistema.getJornadas()) {
                            System.out.println(j);
                            for (OrdenServicio o : j.getOrdenes()) System.out.println("  " + o.getCodigo());
                        }
                        break;
                    case 12: System.out.print(sistema.reporteOrdenes()); break;
                    case 13: System.out.print(sistema.reporteIngresos()); break;
                    case 14: System.out.print(sistema.reporteJornadas()); break;
                    case 0: continuar = false; break;
                    default: System.out.println("Opcion invalida.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Operacion rechazada: " + ex.getMessage());
            } catch (NoSuchElementException ex) {
                continuar = false;
                System.out.println("Entrada finalizada.");
            }
        }
        System.out.println("Programa finalizado.");
    }
    private OrdenServicio leerOrden() {
        int categoria = entero("Categoria (1=diagnostico, 2=instalacion, 3=electrica certificada, 4=aire): ");
        if (categoria < 1 || categoria > 4) throw new IllegalArgumentException("Categoria invalida.");
        String codigo = texto("Codigo: "); String cliente = texto("Cliente: "); String direccion = texto("Direccion: ");
        double horas = decimal("Horas estimadas: ");
        int zonaNumero = entero("Zona (1=metropolitana, 2=periferica): ");
        if (zonaNumero != 1 && zonaNumero != 2) throw new IllegalArgumentException("Zona invalida.");
        Zona zona = zonaNumero == 1 ? Zona.METROPOLITANA : Zona.PERIFERICA;
        // El selector solo construye objetos. Todas las reglas se resuelven por polimorfismo.
        switch (categoria) {
            case 1: return new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2: return new Instalacion(codigo, cliente, direccion, horas, zona, entero("Puntos: "));
            case 3: return new InstalacionCertificada(codigo, cliente, direccion, horas, zona,
                    entero("Puntos: "), monto("Valor declarado de materiales: "));
            case 4: return new MantenimientoAire(codigo, cliente, direccion, horas, zona, entero("BTU: "));
            default: throw new IllegalArgumentException("Categoria invalida.");
        }
    }
    private String texto(String mensaje) {
        System.out.print(mensaje);
        return Validaciones.texto(entrada.nextLine(), "La entrada");
    }
    private int entero(String mensaje) {
        while (true) try { return Integer.parseInt(texto(mensaje)); }
        catch (NumberFormatException ex) { System.out.println("Ingrese un numero entero valido."); }
    }
    private double decimal(String mensaje) {
        while (true) try {
            double valor = Double.parseDouble(texto(mensaje).replace(',', '.'));
            if (!Double.isFinite(valor)) throw new NumberFormatException();
            return valor;
        } catch (NumberFormatException ex) { System.out.println("Ingrese un numero decimal finito valido."); }
    }
    private BigDecimal monto(String mensaje) {
        while (true) try { return new BigDecimal(texto(mensaje).replace(',', '.')); }
        catch (NumberFormatException ex) { System.out.println("Ingrese un monto valido."); }
    }
    private boolean siNo(String mensaje) {
        while (true) {
            String valor = texto(mensaje);
            if (valor.equalsIgnoreCase("s")) return true;
            if (valor.equalsIgnoreCase("n")) return false;
            System.out.println("Responda s o n.");
        }
    }
}
