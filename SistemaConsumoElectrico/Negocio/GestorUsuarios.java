package SistemaConsumoElectrico.Negocio;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;
import SistemaConsumoElectrico.Modelo.*;

import java.util.ArrayList;

public class GestorUsuarios {

    private ArrayList<Usuario> usuarios;

    public GestorUsuarios() {
        usuarios = new ArrayList<>();
        try {
            Usuario administrador = new UsuarioEmpresa(
                    "1710034065", "Administracion", "admin123",
                    Sector.CENTRO, "1799999999001", "Administración del sistema",
                    Rol.ADMINISTRADOR);
            usuarios.add(administrador);

            // Cargar catálogo de dispositivos para asignar a inmuebles de prueba
            CatalogoDispositivos catalogo = new CatalogoDispositivos();

            String[] nombres = {"Carlos","María","Luis","Ana","Juan","Lucía","Pedro","Sofía","Jorge","Valentina","Diego","Camila","Andrés","Gabriela","Ricardo","Paula","Martín","Isabella","Hernán","Julia"};
            String[] apellidos = {"Pérez","Gómez","Rodríguez","Torres","Ramírez","Flores","Sánchez","Vargas","Moreno","Rivas"};
            String[] actividades = {"Restaurante","Comercio","Oficina","Taller","Hotel","Farmacia","Supermercado"};
            Sector[] sectores = Sector.values();
            TipoInmueble[] tipos = TipoInmueble.values();

            java.util.Random rand = new java.util.Random(12345);

            // Generar 60 usuarios variados (aprox. 1 de cada 5 empresarial)
            for (int i = 1; i <= 60; i++) {
                boolean esEmpresa = (i % 5 == 0);
                String nombre = nombres[rand.nextInt(nombres.length)] + " " + apellidos[rand.nextInt(apellidos.length)];
                String cedula = generarCedulaValida(rand.nextInt(24) + 1, rand);
                Sector sector = sectores[rand.nextInt(sectores.length)];
                try {
                    
                    if (esEmpresa) {
                        
                        String ruc = String.format("%013d", 1790000000000L + i);
                        UsuarioEmpresa ue = new UsuarioEmpresa(cedula, nombre, "pass" + i, sector, ruc, actividades[rand.nextInt(actividades.length)]);
                        // agregar un inmueble con 1-3 dispositivos
                        Inmueble inm = new Inmueble(i, "Local " + i, tipos[rand.nextInt(tipos.length)]);
                        int dispositivosAAgregar = 1 + rand.nextInt(3);
                        for (int d = 0; d < dispositivosAAgregar; d++) {
                            Dispositivo disp = catalogo.buscarDispositivo(1 + rand.nextInt(8));
                            if (disp != null) inm.agregarDispositivo(disp);
                        }
                        ue.agregarInmueble(inm);
                        usuarios.add(ue);
                    } else {
                        UsuarioResidencial ur = new UsuarioResidencial(cedula, nombre, "pass" + i, sector);
                        Inmueble inm = new Inmueble(i, "Hogar " + i, tipos[rand.nextInt(tipos.length)]);
                        int dispositivosAAgregar = 1 + rand.nextInt(4);
                        for (int d = 0; d < dispositivosAAgregar; d++) {
                            Dispositivo disp = catalogo.buscarDispositivo(1 + rand.nextInt(8));
                            if (disp != null) inm.agregarDispositivo(disp);
                        }
                        ur.agregarInmueble(inm);
                        usuarios.add(ur);
                    }
                } catch (DatoInvalidoException ex) {
                    // Si falla la creación de un usuario de prueba, continuar con el siguiente
                    ex.printStackTrace();
                }
            }
        } catch (DatoInvalidoException e) {
            e.printStackTrace();
        }
    }

    // Genera una cédula ecuatoriana válida dado un número de provincia (1-24)
    private String generarCedulaValida(int provincia, java.util.Random rand) {
        if (provincia < 1 || provincia > 24) provincia = 1;
        int[] digitos = new int[10];
        digitos[0] = provincia / 10;
        digitos[1] = provincia % 10;
        for (int i = 2; i < 9; i++) digitos[i] = rand.nextInt(10);
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int d = digitos[i];
            if (i % 2 == 0) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            suma += d;
        }
        int decenaSuperior = ((suma / 10) + 1) * 10;
        int digitoVerificador = decenaSuperior - suma;
        if (digitoVerificador == 10) digitoVerificador = 0;
        digitos[9] = digitoVerificador;
        StringBuilder sb = new StringBuilder();
        for (int d : digitos) sb.append(d);
        return sb.toString();
    }

    public void agregarUsuario(Usuario usuario) throws DatoInvalidoException {
        if (usuario == null) {
            throw new DatoInvalidoException("No se puede registrar un usuario vacío");
        }
        Usuario existente = buscarUsuario(usuario.getCedula());
        if (existente != null) {
            if (!existente.isActivo()) {
                existente.setActivo(true);
                return;
            }
            throw new DatoInvalidoException("Ya existe un usuario registrado con esa cédula");
        }
        usuarios.add(usuario);
    }

    public Usuario buscarUsuario(String cedula) {
        for (Usuario u : usuarios) {
            if (u.getCedula().equals(cedula)) return u;
        }
        return null;
    }

    public Usuario validarLogin(String cedula, String contraseña) throws DatoInvalidoException {
        Usuario usuario = buscarUsuario(cedula);
        if (usuario == null) {
            throw new DatoInvalidoException("La cédula no se encuentra registrada");
        }
        if (!usuario.getContraseña().equals(contraseña)) {
            throw new DatoInvalidoException("Contraseña incorrecta");
        }
        return usuario;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }
}
