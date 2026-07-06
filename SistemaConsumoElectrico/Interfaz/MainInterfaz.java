package SistemaConsumoElectrico.Interfaz;

import SistemaConsumoElectrico.Modelo.*;
import SistemaConsumoElectrico.Negocio.*;
import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class
MainInterfaz extends JFrame {
    private SistemaEnergetico    sistema;
    private CatalogoDispositivos catalogo;
    private Usuario              usuarioActual;
    private Inmueble             inmuebleActual;
    private JPanel               panelInicio;
    private JTabbedPane          pestanas;
    private JTable               tablaDisp;
    private DefaultTableModel    modeloDisp;
    private Map<String, Double>  consumoSemanalRetoPorUsuario;
    private Map<String, LinkedHashSet<String>> nivelesRetoPorUsuario;

    private static final Color COLOR_PRIMARIO       = new Color(21, 101, 192);
    private static final Color COLOR_SECUNDARIO     = new Color(56, 142,  60);
    private static final Color COLOR_BARRA_SUPERIOR = new Color( 8,  18,  48);
    private static final Color COLOR_FONDO          = new Color(238, 242, 250);
    private static final Color COLOR_TEXTO          = new Color( 20,  28,  50);
    private static final Color COLOR_TEXTO_SUAVE    = new Color(100, 110, 135);
    private static final Color COLOR_BORDE          = new Color(210, 218, 235);
    private static final Color COLOR_PELIGRO        = new Color(198,  40,  40);
    private static final Color COLOR_SELECCION      = new Color(187, 222, 251);
    private static final Color COLOR_FILA_PAR       = new Color(245, 248, 255);
    private static final Color COLOR_ENCABEZADO_TABLA = new Color(21, 101, 192);

    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_TABLA  = new Font("Segoe UI", Font.PLAIN, 13);

    public MainInterfaz() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            sistema = new SistemaEnergetico();
            catalogo = new CatalogoDispositivos();
            consumoSemanalRetoPorUsuario = new HashMap<>();
            nivelesRetoPorUsuario = new HashMap<>();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    construirMensajeError(e),
                    "Error de inicialización",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
        setTitle("Sistema de Consumo Electrico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1060, 800);
        setLocationRelativeTo(null);
        setBackground(COLOR_FONDO);
        mostrarPantallaLogin();
        setVisible(true);
    }

    private void mostrarPantallaLogin() {

        // ── Fondo con gradiente oscuro y patrón de cuadrícula ───────────────
        JPanel fondoPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(6, 14, 46),
                                                     getWidth(), getHeight(), new Color(10, 58, 128));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Cuadrícula sutil
                g2.setColor(new Color(255, 255, 255, 10));
                g2.setStroke(new BasicStroke(1f));
                for (int x = 0; x < getWidth();  x += 55) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 55) g2.drawLine(0, y, getWidth(), y);
            }
        };

        // ── Tarjeta central ──────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 250), 1),
                BorderFactory.createEmptyBorder(48, 58, 48, 58)));
        card.setPreferredSize(new Dimension(440, 570));
        card.setMaximumSize(new Dimension(440, 570));

        // Icono
        JLabel icono = new JLabel("⚡");
        icono.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        icono.setForeground(COLOR_PRIMARIO);
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título
        JLabel titulo = new JLabel("Consumo Eléctrico");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(8, 20, 60));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo
        JLabel subtitulo = new JLabel("Monitoreo Inteligente de Energía");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(COLOR_TEXTO_SUAVE);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divisor
        JPanel divider = new JPanel();
        divider.setBackground(new Color(218, 228, 248));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(320, 1));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formularioLogin = new JPanel();
        formularioLogin.setLayout(new BoxLayout(formularioLogin, BoxLayout.Y_AXIS));
        formularioLogin.setOpaque(false);
        formularioLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Campos ──────────────────────────────────────────────────────────
        JLabel lblCedula = crearEtiquetaFormulario("Cédula");
        lblCedula.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField txtCedula = new JTextField();
        estilizarTextField(txtCedula);
        txtCedula.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtCedula.setMaximumSize(new Dimension(280, 40));
        txtCedula.setPreferredSize(new Dimension(280, 40));

        JLabel lblPassword = crearEtiquetaFormulario("Contraseña");
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField txtPassword = new JPasswordField();
        estilizarTextField(txtPassword);
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(280, 40));
        txtPassword.setPreferredSize(new Dimension(280, 40));

        // ── Botones ──────────────────────────────────────────────────────────
        JButton btnIngresar = crearBotonPrimario("  Ingresar al Sistema  ", 280, 45);
        JButton btnRegistrar = crearBotonSecundario("Crear cuenta nueva", 280, 40);
        btnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnIngresar.addActionListener(e -> {
            try {
                String cedula     = txtCedula.getText().trim();
                String contraseña = new String(txtPassword.getPassword());
                usuarioActual = sistema.getGestorUsuarios().validarLogin(cedula, contraseña);
                mostrarPantallaPrincipal();
            } catch (DatoInvalidoException ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Error de ingreso", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegistrar.addActionListener(e -> mostrarRegistro());
        txtPassword.addActionListener(e -> btnIngresar.doClick());

        // ── Ensamblaje ───────────────────────────────────────────────────────
        card.add(icono);
        card.add(Box.createVerticalStrut(10));
        card.add(titulo);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitulo);
        card.add(Box.createVerticalStrut(24));
        card.add(divider);
        card.add(Box.createVerticalStrut(24));

        formularioLogin.add(lblCedula);
        formularioLogin.add(Box.createVerticalStrut(6));
        formularioLogin.add(txtCedula);
        formularioLogin.add(Box.createVerticalStrut(18));
        formularioLogin.add(lblPassword);
        formularioLogin.add(Box.createVerticalStrut(6));
        formularioLogin.add(txtPassword);
        formularioLogin.add(Box.createVerticalStrut(28));
        formularioLogin.add(btnIngresar);
        formularioLogin.add(Box.createVerticalStrut(10));
        formularioLogin.add(btnRegistrar);

        card.add(formularioLogin);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        fondoPanel.add(card, gbc);
        setContentPane(fondoPanel);
        revalidate();
        repaint();
    }


    private void mostrarRegistro() {

        // ── Campos ───────────────────────────────────────────────────────────
        JTextField cedula        = new JTextField();
        JTextField nombre        = new JTextField();
        JPasswordField contraseña = new JPasswordField();
        JComboBox<Sector> sector  = new JComboBox<>(Sector.values());
        JComboBox<String> tipo    = new JComboBox<>(new String[]{"Residencial", "Empresa"});
        JTextField ruc            = new JTextField();
        JTextField actividad      = new JTextField();
        JTextField nombreInmueble = new JTextField();
        JComboBox<TipoInmueble> tipoInmueble = new JComboBox<>(TipoInmueble.values());

        ruc.setEnabled(false);
        actividad.setEnabled(false);

        tipo.addActionListener(e -> {
            boolean empresa = tipo.getSelectedItem().equals("Empresa");
            ruc.setEnabled(empresa);
            actividad.setEnabled(empresa);
            if (!empresa) { ruc.setText(""); actividad.setText(""); }
        });

        // ── Panel del formulario ─────────────────────────────────────────────
        Font lblFont = new Font("Segoe UI", Font.BOLD, 13);

        JPanel grid = new JPanel(new GridLayout(9, 2, 10, 10));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] etiquetas = {
            "Tipo de usuario", "Cédula", "Nombre", "Contraseña",
            "Sector", "RUC (empresa)", "Actividad económica",
            "Nombre del inmueble", "Tipo de inmueble"
        };
        JComponent[] componentes = {
            tipo, cedula, nombre, contraseña,
            sector, ruc, actividad,
            nombreInmueble, tipoInmueble
        };

        for (int i = 0; i < etiquetas.length; i++) {
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setFont(lblFont);
            lbl.setForeground(COLOR_TEXTO);
            grid.add(lbl);
            if (componentes[i] instanceof JTextField) {
                estilizarTextField((JTextField) componentes[i]);
            }
            grid.add(componentes[i]);
        }

        int opcion = JOptionPane.showConfirmDialog(this, grid, "Registro de usuario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                Usuario usuario;
                if (tipo.getSelectedItem().equals("Residencial")) {
                    usuario = new UsuarioResidencial(cedula.getText().trim(), nombre.getText().trim(),
                            new String(contraseña.getPassword()), (Sector) sector.getSelectedItem());
                } else {
                    usuario = new UsuarioEmpresa(cedula.getText().trim(), nombre.getText().trim(),
                            new String(contraseña.getPassword()), (Sector) sector.getSelectedItem(),
                            ruc.getText().trim(), actividad.getText().trim());
                }
                if (nombreInmueble.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del inmueble.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Inmueble inmueble = new Inmueble(generarIdInmueble(), nombreInmueble.getText().trim(),
                        (TipoInmueble) tipoInmueble.getSelectedItem());
                usuario.agregarInmueble(inmueble);
                sistema.getGestorUsuarios().agregarUsuario(usuario);
                JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
            } catch (DatoInvalidoException ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel crearPanelUsuarios() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("Administración de usuarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblEliminar = new JLabel("Cédula del usuario:");
        lblEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTextField tfEliminarId = new JTextField();
        estilizarTextField(tfEliminarId);

        JButton btnEliminar = crearBotonPeligro("Desactivar", 140, 38);
        JButton btnReactivar = crearBotonExito("Reactivar",   140, 38);
        JButton btnMostrar   = crearBotonPrimario("Actualizar listado", 175, 38);

        String[] columnas = {"Cédula", "Nombre", "Rol", "Tipo", "Sector", "Estado", "RUC", "Actividad"};

        DefaultTableModel modeloUsuarios = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                return false;

            }

        };

        JTable tablaUsuarios = new JTable(modeloUsuarios);
        estilizarTabla(tablaUsuarios);
        JScrollPane scrollUsuarios = new JScrollPane(tablaUsuarios);
        scrollUsuarios.setPreferredSize(new Dimension(0, 230));
        scrollUsuarios.setVisible(false);

        btnMostrar.addActionListener(e -> {

            modeloUsuarios.setRowCount(0);

            for (Usuario u : sistema.getGestorUsuarios().getUsuarios()) {

                if (u.getRol() == Rol.ADMINISTRADOR) {
                    continue;
                }

                String rol = u.getRol().toString();

                String tipo = (u instanceof UsuarioEmpresa) ? "Empresa" : "Residencial";

                String estado = u.isActivo() ? "Activo" : "Inactivo";

                String ruc = "";
                String actividad = "";

                if (u instanceof UsuarioEmpresa) {

                    UsuarioEmpresa emp = (UsuarioEmpresa) u;

                    ruc = emp.getRuc();
                    actividad = emp.getActividadEconomica();

                }

                modeloUsuarios.addRow(new Object[]{

                        u.getCedula(), u.getNombre(), rol, tipo, u.getSector(), estado, ruc, actividad

                });

            }

            scrollUsuarios.setVisible(true);

            panel.revalidate();
            panel.repaint();

        });
        btnEliminar.addActionListener(e -> {

            try {

                String cedula = tfEliminarId.getText().trim();

                if (cedula.isEmpty()) {

                    JOptionPane.showMessageDialog(panel, "Ingrese la cédula del usuario a gestionar.");

                    return;

                }

                Usuario usuario = sistema.getGestorUsuarios().buscarUsuario(cedula);

                if (usuario == null) {

                    JOptionPane.showMessageDialog(panel, "No existe un usuario registrado con esa cédula.");

                    return;

                }

                if (usuario.getRol() == Rol.ADMINISTRADOR) {

                    JOptionPane.showMessageDialog(panel, "No es posible desactivar la cuenta del administrador.");

                    return;

                }

                if (!usuario.isActivo()) {

                    JOptionPane.showMessageDialog(panel, "La cuenta ya se encuentra desactivada.");

                    return;

                }

                usuario.setActivo(false);

                JOptionPane.showMessageDialog(panel, "La cuenta fue desactivada correctamente.");

                tfEliminarId.setText("");

                if (scrollUsuarios.isVisible()) {

                    btnMostrar.doClick();

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        panel,
                        construirMensajeError(ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

            }

        });

        btnReactivar.addActionListener(e -> {

            try {

                String cedula = tfEliminarId.getText().trim();

                if (cedula.isEmpty()) {

                    JOptionPane.showMessageDialog(panel, "Ingrese la cédula del usuario a gestionar.");

                    return;

                }

                Usuario usuario = sistema.getGestorUsuarios().buscarUsuario(cedula);

                if (usuario == null) {

                    JOptionPane.showMessageDialog(panel, "No existe un usuario registrado con esa cédula.");

                    return;

                }

                if (usuario.isActivo()) {

                    JOptionPane.showMessageDialog(panel, "La cuenta ya se encuentra activa.");

                    return;

                }

                usuario.setActivo(true);

                JOptionPane.showMessageDialog(panel, "La cuenta fue reactivada correctamente.");

                tfEliminarId.setText("");

                if (scrollUsuarios.isVisible()) {

                    btnMostrar.doClick();

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        panel,
                        construirMensajeError(ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

            }

        });

        JLabel lblDescripcion = new JLabel("Gestione el estado de las cuentas de usuarios del sistema.");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescripcion.setForeground(COLOR_TEXTO_SUAVE);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        panelAcciones.setBackground(COLOR_FONDO);

        panelAcciones.add(btnEliminar);
        panelAcciones.add(btnReactivar);
        panelAcciones.add(btnMostrar);

        panelSuperior.add(lblTitulo);
        panelSuperior.add(Box.createVerticalStrut(6));
        panelSuperior.add(lblDescripcion);
        panelSuperior.add(Box.createVerticalStrut(12));
        panelSuperior.add(lblEliminar);
        panelSuperior.add(Box.createVerticalStrut(5));
        panelSuperior.add(tfEliminarId);
        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(panelAcciones);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollUsuarios, BorderLayout.CENTER);

        return panel;

    }

    private void mostrarPantallaPrincipal() {

        // NUEVO: seleccionar automáticamente el primer inmueble del usuario
        if (usuarioActual != null && usuarioActual.getRol() != Rol.ADMINISTRADOR) {

            if (!usuarioActual.getInmuebles().isEmpty()) {

                inmuebleActual = usuarioActual.getInmuebles().get(0);

            } else {

                inmuebleActual = null;

            }

        }

        PanelConFondoPCB panelPrincipal = new PanelConFondoPCB();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.setOpaque(false);

        JPanel panelEncabezado = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, COLOR_BARRA_SUPERIOR,
                                                     getWidth(), 0, new Color(21, 60, 112));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelEncabezado.setOpaque(false);
        panelEncabezado.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        panelEncabezado.setPreferredSize(new Dimension(0, 88));

        JLabel lblBienvenida = new JLabel("Bienvenido, " + usuarioActual.getNombre());
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBienvenida.setForeground(Color.WHITE);

        String estado = usuarioActual.isActivo() ? "✔ Activo" : "✘ Inactivo";

        JLabel lblInfo = new JLabel("Cédula: " + usuarioActual.getCedula()
                + "   ·   Sector: " + usuarioActual.getSector()
                + "   ·   Rol: " + usuarioActual.getRol()
                + "   ·   " + estado);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(new Color(195, 215, 245));

        JPanel panelIzq = new JPanel();
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setOpaque(false);

        panelIzq.add(lblBienvenida);
        panelIzq.add(Box.createVerticalStrut(5));
        panelIzq.add(lblInfo);

        JButton btnSalir = crearBotonPeligro("✕  Cerrar Sesión", 155, 40);

        btnSalir.addActionListener(e -> {
            usuarioActual = null;
            inmuebleActual = null;
            mostrarPantallaLogin();
        });

        panelEncabezado.add(panelIzq, BorderLayout.WEST);
        panelEncabezado.add(btnSalir, BorderLayout.EAST);

        pestanas = new JTabbedPane();
        pestanas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pestanas.setBackground(COLOR_FONDO);
        pestanas.setForeground(COLOR_TEXTO);

        if (usuarioActual.getRol() == Rol.ADMINISTRADOR) {

            pestanas.addTab("Usuarios", crearPanelUsuarios());
            pestanas.addTab("Catálogo de dispositivos", crearPanelCatalogoDispositivos());

            pestanas.addTab("Reportes", crearPanelReportes());

        } else {

            if (usuarioActual.isActivo()) {

                pestanas.addTab("Inicio", crearPestanaInicio());

                pestanas.addTab("Dispositivos", crearPestanaDispositivos());

                pestanas.addTab("Resumen vivienda", crearPestanaResumen());

                pestanas.addTab("Factura vivienda", crearPestanaFactura());

                pestanas.addTab("Recomendaciones vivienda", crearPestanaRecomendaciones());

                pestanas.addTab("Consumo global", crearPestanaConsumoGlobal());

                pestanas.addTab("Retos de ahorro", crearPestanaRetos());

            } else {

                JOptionPane.showMessageDialog(this, "Su cuenta se encuentra desactivada.\n\n" + "Solo puede consultar la información histórica de su cuenta.", "Cuenta desactivada", JOptionPane.INFORMATION_MESSAGE);

                pestanas.addTab("Resumen vivienda", crearPestanaResumen());

                pestanas.addTab("Factura vivienda", crearPestanaFactura());

                pestanas.addTab("Consumo global", crearPestanaConsumoGlobal());

            }

        }

        panelContenedor.add(crearPanelMarca(), BorderLayout.NORTH);

        panelContenedor.add(panelEncabezado, BorderLayout.CENTER);

        panelPrincipal.add(panelContenedor, BorderLayout.NORTH);

        panelPrincipal.add(pestanas, BorderLayout.CENTER);

        setContentPane(panelPrincipal);

        revalidate();
        repaint();
    }

    private JPanel crearPanelReportes() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel lblTitulo = new JLabel("Centro de analítica y reportes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel lblSubtitulo = new JLabel("Visualice información detallada por usuario y análisis global con filtros avanzados.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(COLOR_TEXTO_SUAVE);

        JPanel panelCabecera = new JPanel();
        panelCabecera.setLayout(new BoxLayout(panelCabecera, BoxLayout.Y_AXIS));
        panelCabecera.setBackground(COLOR_FONDO);
        panelCabecera.add(lblTitulo);
        panelCabecera.add(Box.createVerticalStrut(4));
        panelCabecera.add(lblSubtitulo);
        panelCabecera.add(Box.createVerticalStrut(8));

        final JLabel lblModoUsuario = new JLabel("");
        lblModoUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblModoUsuario.setForeground(new Color(80, 80, 80));
        panelCabecera.add(lblModoUsuario);
        panelCabecera.add(Box.createVerticalStrut(6));

        JPanel panelFiltros = new JPanel(new GridLayout(4, 4, 10, 8));
        panelFiltros.setBackground(Color.WHITE);
        panelFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Filtros de consulta"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JComboBox<String> cbSectorFiltro = new JComboBox<>();
        cbSectorFiltro.addItem("TODOS");
        for (Sector sector : Sector.values()) {
            cbSectorFiltro.addItem(sector.name());
        }
        cbSectorFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JComboBox<String> cbTipoCuenta = new JComboBox<>(new String[]{"TODOS", "RESIDENCIAL", "EMPRESA"});
        cbTipoCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JComboBox<String> cbNivelConsumo = new JComboBox<>(new String[]{"TODOS", "BAJO", "NORMAL", "ALTO", "CRITICO"});
        cbNivelConsumo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTextField tfKwhMin = new JTextField();
        JTextField tfKwhMax = new JTextField();
        JTextField tfMinInmuebles = new JTextField();
        JTextField tfMinDispositivos = new JTextField();
        JTextField tfCedulaUsuario = new JTextField();
        estilizarTextField(tfKwhMin);
        estilizarTextField(tfKwhMax);
        estilizarTextField(tfMinInmuebles);
        estilizarTextField(tfMinDispositivos);
        estilizarTextField(tfCedulaUsuario);

        panelFiltros.add(new JLabel("Sector:"));
        panelFiltros.add(cbSectorFiltro);
        panelFiltros.add(new JLabel("Tipo de cuenta:"));
        panelFiltros.add(cbTipoCuenta);
        panelFiltros.add(new JLabel("Nivel de consumo:"));
        panelFiltros.add(cbNivelConsumo);
        panelFiltros.add(new JLabel("Consumo mínimo (kWh/mes):"));
        panelFiltros.add(tfKwhMin);
        panelFiltros.add(new JLabel("Consumo máximo (kWh/mes):"));
        panelFiltros.add(tfKwhMax);
        panelFiltros.add(new JLabel("Mínimo de inmuebles:"));
        panelFiltros.add(tfMinInmuebles);
        panelFiltros.add(new JLabel("Mínimo de dispositivos:"));
        panelFiltros.add(tfMinDispositivos);
        panelFiltros.add(new JLabel("Cédula (análisis individual):"));
        panelFiltros.add(tfCedulaUsuario);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panelAcciones.setBackground(COLOR_FONDO);
        JButton btnAplicarFiltros = crearBotonPrimario("Aplicar filtros", -1, 38);
        JButton btnTopConsumo = crearBotonPrimario("Top consumo", -1, 38);
        JButton btnAnalisisUsuario = crearBotonPrimario("Análisis por usuario", -1, 38);
        JButton btnAnalisisGlobal = crearBotonPrimario("Análisis global", -1, 38);
        JButton btnUsoDispositivos = crearBotonPrimario("Análisis de dispositivos", -1, 38);
        JButton btnLimpiar = crearBotonAdvertencia("Limpiar filtros", -1, 38);
        panelAcciones.add(btnAplicarFiltros);
        panelAcciones.add(btnTopConsumo);
        panelAcciones.add(btnAnalisisUsuario);
        panelAcciones.add(btnAnalisisGlobal);
        panelAcciones.add(btnUsoDispositivos);
        panelAcciones.add(btnLimpiar);

        JPanel panelSuperior = new JPanel(new BorderLayout(0, 8));
        panelSuperior.setBackground(COLOR_FONDO);
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        panelSuperior.add(panelAcciones, BorderLayout.SOUTH);

        JTextPane areaReporte = crearAreaInforme();
        areaReporte.setText(formatearReporteAdministrativoHtml(
                "Panel de reportes gerenciales.\n" +
                "Configure los filtros y seleccione el tipo de análisis requerido."
        ));

        JScrollPane scroll = new JScrollPane(areaReporte);
        scroll.setPreferredSize(new Dimension(0, 420));

        JPanel panelVisualizaciones = new JPanel(new GridLayout(1, 2, 12, 0));
        panelVisualizaciones.setBackground(COLOR_FONDO);
        panelVisualizaciones.setBorder(BorderFactory.createTitledBorder("Visualización gráfica"));
        panelVisualizaciones.setPreferredSize(new Dimension(0, 280));
        actualizarGraficosReportesAdmin(panelVisualizaciones, obtenerUsuariosNoAdmin(), "GLOBAL");

        btnAplicarFiltros.addActionListener(e -> {
            try {
                Sector sectorFiltro = obtenerSectorDesdeTexto((String) cbSectorFiltro.getSelectedItem());
                String tipoCuentaFiltro = (String) cbTipoCuenta.getSelectedItem();
                String nivelFiltro = (String) cbNivelConsumo.getSelectedItem();
                Double consumoMinimo = leerDoubleOpcional(tfKwhMin.getText(), "Consumo mínimo");
                Double consumoMaximo = leerDoubleOpcional(tfKwhMax.getText(), "Consumo máximo");
                Integer minimoInmuebles = leerEnteroOpcional(tfMinInmuebles.getText(), "Mínimo de inmuebles");
                Integer minimoDispositivos = leerEnteroOpcional(tfMinDispositivos.getText(), "Mínimo de dispositivos");

                if (consumoMinimo != null && consumoMaximo != null && consumoMinimo > consumoMaximo) {
                    throw new NumberFormatException("El consumo mínimo no puede ser mayor al consumo máximo.");
                }

                String cedulaBusqueda = tfCedulaUsuario.getText().trim();
                List<Usuario> filtrados;
                if (!cedulaBusqueda.isEmpty()) {
                    Usuario u = sistema.getGestorUsuarios().buscarUsuario(cedulaBusqueda);
                    if (u == null || u.getRol() == Rol.ADMINISTRADOR) {
                        JOptionPane.showMessageDialog(this, "No se encontró un usuario válido con la cédula indicada.");
                        return;
                    }
                    filtrados = new ArrayList<>();
                    filtrados.add(u);
                    lblModoUsuario.setText("Modo: Usuario - " + u.getNombre() + " (" + u.getCedula() + ")");
                } else {
                    filtrados = filtrarUsuariosReportes(
                            sectorFiltro, tipoCuentaFiltro, nivelFiltro,
                            consumoMinimo, consumoMaximo, minimoInmuebles, minimoDispositivos
                    );
                    lblModoUsuario.setText("");
                }

                String reporte = generarReporteUsuariosDetalladoFiltrado(
                        filtrados, sectorFiltro, tipoCuentaFiltro, nivelFiltro,
                        consumoMinimo, consumoMaximo, minimoInmuebles, minimoDispositivos
                );
                areaReporte.setText(formatearReporteAdministrativoHtml(reporte));
                areaReporte.setCaretPosition(0);
                actualizarGraficosReportesAdmin(panelVisualizaciones, filtrados, "FILTROS");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Filtro inválido", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnTopConsumo.addActionListener(e -> {
            try {
                Sector sectorFiltro = obtenerSectorDesdeTexto((String) cbSectorFiltro.getSelectedItem());
                String tipoCuentaFiltro = (String) cbTipoCuenta.getSelectedItem();
                String nivelFiltro = (String) cbNivelConsumo.getSelectedItem();
                Double consumoMinimo = leerDoubleOpcional(tfKwhMin.getText(), "Consumo mínimo");
                Double consumoMaximo = leerDoubleOpcional(tfKwhMax.getText(), "Consumo máximo");
                Integer minimoInmuebles = leerEnteroOpcional(tfMinInmuebles.getText(), "Mínimo de inmuebles");
                Integer minimoDispositivos = leerEnteroOpcional(tfMinDispositivos.getText(), "Mínimo de dispositivos");

                String cedulaBusqueda = tfCedulaUsuario.getText().trim();
                List<Usuario> filtrados;
                if (!cedulaBusqueda.isEmpty()) {
                    Usuario u = sistema.getGestorUsuarios().buscarUsuario(cedulaBusqueda);
                    if (u == null || u.getRol() == Rol.ADMINISTRADOR) {
                        JOptionPane.showMessageDialog(this, "No se encontró un usuario válido con la cédula indicada.");
                        return;
                    }
                    filtrados = new ArrayList<>();
                    filtrados.add(u);
                    lblModoUsuario.setText("Modo: Usuario - " + u.getNombre() + " (" + u.getCedula() + ")");
                } else {
                    filtrados = filtrarUsuariosReportes(
                            sectorFiltro, tipoCuentaFiltro, nivelFiltro,
                            consumoMinimo, consumoMaximo, minimoInmuebles, minimoDispositivos
                    );
                    lblModoUsuario.setText("");
                }
                String reporte = generarTopConsumoUsuarios(filtrados, 5);
                areaReporte.setText(formatearReporteAdministrativoHtml(reporte));
                areaReporte.setCaretPosition(0);
                actualizarGraficosReportesAdmin(panelVisualizaciones, filtrados, "TOP");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Filtro inválido", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAnalisisUsuario.addActionListener(e -> {
            String cedula = tfCedulaUsuario.getText().trim();
            if (cedula.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la cédula para generar el análisis individual.");
                return;
            }
            Usuario usuario = sistema.getGestorUsuarios().buscarUsuario(cedula);
            if (usuario == null || usuario.getRol() == Rol.ADMINISTRADOR) {
                JOptionPane.showMessageDialog(this, "No se encontró un usuario válido con la cédula indicada.");
                return;
            }
            try {
                // Obtener filtros activos para respetarlos en el análisis individual
                Sector sectorFiltro = obtenerSectorDesdeTexto((String) cbSectorFiltro.getSelectedItem());
                String tipoCuentaFiltro = (String) cbTipoCuenta.getSelectedItem();
                String nivelFiltro = (String) cbNivelConsumo.getSelectedItem();
                Double consumoMinimo = leerDoubleOpcional(tfKwhMin.getText(), "Consumo mínimo");
                Double consumoMaximo = leerDoubleOpcional(tfKwhMax.getText(), "Consumo máximo");
                Integer minimoInmuebles = leerEnteroOpcional(tfMinInmuebles.getText(), "Mínimo de inmuebles");
                Integer minimoDispositivos = leerEnteroOpcional(tfMinDispositivos.getText(), "Mínimo de dispositivos");

                List<Usuario> filtrados = filtrarUsuariosReportes(
                        sectorFiltro, tipoCuentaFiltro, nivelFiltro,
                        consumoMinimo, consumoMaximo, minimoInmuebles, minimoDispositivos
                );

                // Si hay filtros aplicados y el usuario no está en la lista filtrada, notificar y no mostrar el análisis
                boolean incluido = filtrados.isEmpty() || filtrados.stream().anyMatch(u -> u.getCedula().equals(usuario.getCedula()));
                if (!incluido) {
                    JOptionPane.showMessageDialog(this, "El usuario no cumple los filtros aplicados. Ajuste los filtros o limpie para ver su análisis.");
                    return;
                }

                List<Usuario> individual = new ArrayList<>();
                individual.add(usuario);
                lblModoUsuario.setText("Modo: Usuario - " + usuario.getNombre() + " (" + usuario.getCedula() + ")");

                String reporte = generarReporteUsuariosDetalladoFiltrado(
                        individual, sectorFiltro, tipoCuentaFiltro, nivelFiltro,
                        consumoMinimo, consumoMaximo, minimoInmuebles, minimoDispositivos
                );
                areaReporte.setText(formatearReporteAdministrativoHtml(reporte));
                areaReporte.setCaretPosition(0);
                actualizarGraficosReportesAdmin(panelVisualizaciones, individual, "USUARIO");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Filtro inválido", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAnalisisGlobal.addActionListener(e -> {
            try {
                Sector sectorFiltro = obtenerSectorDesdeTexto((String) cbSectorFiltro.getSelectedItem());
                String tipoCuentaFiltro = (String) cbTipoCuenta.getSelectedItem();
                String nivelFiltro = (String) cbNivelConsumo.getSelectedItem();
                Double consumoMinimo = leerDoubleOpcional(tfKwhMin.getText(), "Consumo mínimo");
                Double consumoMaximo = leerDoubleOpcional(tfKwhMax.getText(), "Consumo máximo");
                Integer minimoInmuebles = leerEnteroOpcional(tfMinInmuebles.getText(), "Mínimo de inmuebles");
                Integer minimoDispositivos = leerEnteroOpcional(tfMinDispositivos.getText(), "Mínimo de dispositivos");

                String cedulaBusqueda = tfCedulaUsuario.getText().trim();
                List<Usuario> filtrados;
                if (!cedulaBusqueda.isEmpty()) {
                    Usuario u = sistema.getGestorUsuarios().buscarUsuario(cedulaBusqueda);
                    if (u == null || u.getRol() == Rol.ADMINISTRADOR) {
                        JOptionPane.showMessageDialog(this, "No se encontró un usuario válido con la cédula indicada.");
                        return;
                    }
                    filtrados = new ArrayList<>();
                    filtrados.add(u);
                    lblModoUsuario.setText("Modo: Usuario - " + u.getNombre() + " (" + u.getCedula() + ")");
                } else {
                    filtrados = filtrarUsuariosReportes(
                            sectorFiltro, tipoCuentaFiltro, nivelFiltro,
                            consumoMinimo, consumoMaximo, minimoInmuebles, minimoDispositivos
                    );
                    lblModoUsuario.setText("");
                }
                String reporte = generarAnalisisGlobalUsuarios(filtrados);
                areaReporte.setText(formatearReporteAdministrativoHtml(reporte));
                areaReporte.setCaretPosition(0);
                actualizarGraficosReportesAdmin(panelVisualizaciones, filtrados, "GLOBAL");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Filtro inválido", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnUsoDispositivos.addActionListener(e -> {
            try {
                Sector sectorFiltro = obtenerSectorDesdeTexto((String) cbSectorFiltro.getSelectedItem());
                String tipoCuentaFiltro = (String) cbTipoCuenta.getSelectedItem();
                String nivelFiltro = (String) cbNivelConsumo.getSelectedItem();
                Double consumoMinimo = leerDoubleOpcional(tfKwhMin.getText(), "Consumo mínimo");
                Double consumoMaximo = leerDoubleOpcional(tfKwhMax.getText(), "Consumo máximo");
                Integer minimoInmuebles = leerEnteroOpcional(tfMinInmuebles.getText(), "Mínimo de inmuebles");
                Integer minimoDispositivos = leerEnteroOpcional(tfMinDispositivos.getText(), "Mínimo de dispositivos");

                String cedulaBusqueda = tfCedulaUsuario.getText().trim();
                List<Usuario> filtrados;
                if (!cedulaBusqueda.isEmpty()) {
                    Usuario u = sistema.getGestorUsuarios().buscarUsuario(cedulaBusqueda);
                    if (u == null || u.getRol() == Rol.ADMINISTRADOR) {
                        JOptionPane.showMessageDialog(this, "No se encontró un usuario válido con la cédula indicada.");
                        return;
                    }
                    filtrados = new ArrayList<>();
                    filtrados.add(u);
                    lblModoUsuario.setText("Modo: Usuario - " + u.getNombre() + " (" + u.getCedula() + ")");
                } else {
                    filtrados = filtrarUsuariosReportes(
                            sectorFiltro, tipoCuentaFiltro, nivelFiltro,
                            consumoMinimo, consumoMaximo, minimoInmuebles, minimoDispositivos
                    );
                    lblModoUsuario.setText("");
                }
                String reporte = generarAnalisisDispositivosGlobal(filtrados);
                areaReporte.setText(formatearReporteAdministrativoHtml(reporte));
                areaReporte.setCaretPosition(0);
                actualizarGraficosReportesAdmin(panelVisualizaciones, filtrados, "DISPOSITIVOS");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Filtro inválido", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnLimpiar.addActionListener(e -> {
            cbSectorFiltro.setSelectedIndex(0);
            cbTipoCuenta.setSelectedIndex(0);
            cbNivelConsumo.setSelectedIndex(0);
            tfKwhMin.setText("");
            tfKwhMax.setText("");
            tfMinInmuebles.setText("");
            tfMinDispositivos.setText("");
            tfCedulaUsuario.setText("");
            lblModoUsuario.setText("");
            areaReporte.setText(formatearReporteAdministrativoHtml(
                    "Filtros restablecidos.\nSeleccione una acción para generar un nuevo reporte."
            ));
            areaReporte.setCaretPosition(0);
            actualizarGraficosReportesAdmin(panelVisualizaciones, obtenerUsuariosNoAdmin(), "GLOBAL");
        });

        panelCabecera.add(panelSuperior);
        panel.add(panelCabecera, BorderLayout.NORTH);

        JSplitPane splitReportes = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, panelVisualizaciones);
        splitReportes.setResizeWeight(0.62);
        splitReportes.setBorder(BorderFactory.createEmptyBorder());
        splitReportes.setDividerSize(8);
        panel.add(splitReportes, BorderLayout.CENTER);

        return panel;

    }

    private JPanel crearPestanaDispositivos() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new BoxLayout(panelEntrada, BoxLayout.Y_AXIS));
        panelEntrada.setBackground(Color.WHITE);
        panelEntrada.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Agregar Dispositivo"), BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        //==============================
        // SELECCIÓN DE INMUEBLE
        //==============================

        JLabel lblInmueble = new JLabel("Vivienda:");
        lblInmueble.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JComboBox<Inmueble> cbInmuebles = new JComboBox<>();

        for (Inmueble inmueble : usuarioActual.getInmuebles()) {

            cbInmuebles.addItem(inmueble);

        }

        if (inmuebleActual != null && usuarioActual.getInmuebles().contains(inmuebleActual)) {
            cbInmuebles.setSelectedItem(inmuebleActual);
        } else if (!usuarioActual.getInmuebles().isEmpty()) {
            inmuebleActual = usuarioActual.getInmuebles().get(0);
            cbInmuebles.setSelectedItem(inmuebleActual);
        }

        JButton btnNuevoInmueble = crearBotonExito("＋ Nueva vivienda", -1, 36);
        JButton btnEliminarInmueble = crearBotonAdvertencia("－ Eliminar vivienda", -1, 36);

        cbInmuebles.addActionListener(e -> {

            inmuebleActual = (Inmueble) cbInmuebles.getSelectedItem();

            cargarTablaDispositivos(modeloDisp);

            actualizarInicio();

        });

        btnNuevoInmueble.addActionListener(e -> {

            try {

                JTextField txtNombre = new JTextField();

                JComboBox<TipoInmueble> cbTipo = new JComboBox<>(TipoInmueble.values());

                JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 8));

                formulario.add(new JLabel("Nombre"));
                formulario.add(txtNombre);

                formulario.add(new JLabel("Tipo"));
                formulario.add(cbTipo);

                int opcion = JOptionPane.showConfirmDialog(this, formulario, "Nueva vivienda", JOptionPane.OK_CANCEL_OPTION);

                if (opcion != JOptionPane.OK_OPTION) {

                    return;

                }

                if (txtNombre.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(this, "Ingrese un nombre.");

                    return;

                }

                Inmueble nuevo = new Inmueble(generarIdInmueble(), txtNombre.getText().trim(), (TipoInmueble) cbTipo.getSelectedItem());

                usuarioActual.agregarInmueble(nuevo);

                cbInmuebles.addItem(nuevo);

                cbInmuebles.setSelectedItem(nuevo);

                inmuebleActual = nuevo;

                cargarTablaDispositivos(modeloDisp);

                actualizarInicio();

                JOptionPane.showMessageDialog(this, "Vivienda registrada correctamente.");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Error", JOptionPane.ERROR_MESSAGE);

            }

        });

        //==============================
        // DISPOSITIVOS
        //==============================

        JLabel lblDisp = new JLabel("Dispositivo:");
        lblDisp.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JComboBox<String> cbDisp = new JComboBox<>();
        cargarCatalogo(cbDisp);
        cbDisp.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTextField tfCant = new JTextField();
        estilizarTextField(tfCant);

        JLabel lblHoras = new JLabel("Horas Diarias:");
        lblHoras.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTextField tfHoras = new JTextField();
        estilizarTextField(tfHoras);

        JButton btnAgregar  = crearBotonPrimario("＋ Agregar",  -1, 36);
        JButton btnEditar   = crearBotonAdvertencia("✎ Editar", -1, 36);
        JButton btnEliminar = crearBotonPeligro("✕ Eliminar",  -1, 36);

        String[] columnas = {"ID", "Dispositivo", "Potencia (W)", "Cantidad", "Horas/día", "Consumo (kWh/día)"};

        modeloDisp = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                return false;

            }

        };

        tablaDisp = new JTable(modeloDisp);
        estilizarTabla(tablaDisp);
        JScrollPane scroll = new JScrollPane(tablaDisp);

        btnAgregar.addActionListener(e -> {

            try {

                if (inmuebleActual == null) {

                    JOptionPane.showMessageDialog(this, "Primero seleccione o cree una vivienda.");

                    return;

                }

                String seleccionado = (String) cbDisp.getSelectedItem();

                if (seleccionado == null || seleccionado.startsWith("Seleccione")) {

                    JOptionPane.showMessageDialog(this, "Seleccione un dispositivo.");

                    return;

                }

                int cantidad = Integer.parseInt(tfCant.getText().trim());

                double horas = Double.parseDouble(tfHoras.getText().trim());

                if (cantidad <= 0 || horas <= 0 || horas > 24) {

                    JOptionPane.showMessageDialog(this, "Cantidad u horas inválidas.");

                    return;

                }

                Dispositivo nuevo;
                int id = obtenerIdDesdeItem(seleccionado);

                Dispositivo base = catalogo.buscarDispositivo(id);

                if (base == null) {

                    JOptionPane.showMessageDialog(this, "Dispositivo no encontrado.");

                    return;

                }

                nuevo = new Dispositivo(generarIdTemporal(), base.getNombre(), base.getPotencia(), cantidad, horas);

                inmuebleActual.agregarDispositivo(nuevo);

                cargarTablaDispositivos(modeloDisp);

                actualizarInicio();

                tfCant.setText("");
                tfHoras.setText("");

                cbDisp.setSelectedIndex(0);

                JOptionPane.showMessageDialog(this, "Dispositivo agregado correctamente.");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Error", JOptionPane.ERROR_MESSAGE);

            }

        });
        btnEditar.addActionListener(e -> {

            int fila = tablaDisp.getSelectedRow();

            if (fila == -1) {

                JOptionPane.showMessageDialog(this, "Seleccione un dispositivo.");

                return;

            }

            if (inmuebleActual == null) {

                JOptionPane.showMessageDialog(this, "Seleccione una vivienda.");

                return;

            }

            try {

                int id = Integer.parseInt(modeloDisp.getValueAt(fila, 0).toString());

                Dispositivo dispositivoEditar = inmuebleActual.buscarDispositivo(id);

                if (dispositivoEditar == null) {

                    JOptionPane.showMessageDialog(this, "No se encontró el dispositivo.");

                    return;

                }

                String nuevaCantidad = JOptionPane.showInputDialog(this, "Nueva cantidad:", dispositivoEditar.getCantidad());

                if (nuevaCantidad == null) {

                    return;

                }

                String nuevasHoras = JOptionPane.showInputDialog(this, "Nuevas horas de uso por día:", dispositivoEditar.getHorasUsoDiarias());

                if (nuevasHoras == null) {

                    return;

                }

                dispositivoEditar.setCantidad(Integer.parseInt(nuevaCantidad.trim()));

                dispositivoEditar.setHorasUsoDiarias(Double.parseDouble(nuevasHoras.trim()));

                cargarTablaDispositivos(modeloDisp);

                actualizarInicio();

                tablaDisp.clearSelection();

                JOptionPane.showMessageDialog(this, "Dispositivo actualizado correctamente.");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Error", JOptionPane.ERROR_MESSAGE);

            }

        });

        btnEliminar.addActionListener(e -> {

            int fila = tablaDisp.getSelectedRow();

            if (fila == -1) {

                JOptionPane.showMessageDialog(this, "Seleccione un dispositivo.");

                return;

            }

            if (inmuebleActual == null) {

                JOptionPane.showMessageDialog(this, "Seleccione una vivienda.");

                return;

            }

            int opcion = JOptionPane.showConfirmDialog(this, "¿Eliminar el dispositivo seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);

            if (opcion != JOptionPane.YES_OPTION) {

                return;

            }

            try {

                int id = Integer.parseInt(modeloDisp.getValueAt(fila, 0).toString());

                if (inmuebleActual.eliminarDispositivo(id)) {

                    cargarTablaDispositivos(modeloDisp);

                    actualizarInicio();

                    tablaDisp.clearSelection();

                    JOptionPane.showMessageDialog(this, "Dispositivo eliminado correctamente.");

                } else {

                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el dispositivo.");

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Error", JOptionPane.ERROR_MESSAGE);

            }

        });
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));

        panelBotones.setBackground(Color.WHITE);

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

//==============================
// FORMULARIO
//==============================

        panelEntrada.add(lblInmueble);
        panelEntrada.add(Box.createVerticalStrut(5));

        JPanel panelInmueble = new JPanel(new BorderLayout(8, 0));

        panelInmueble.setBackground(Color.WHITE);

        panelInmueble.add(cbInmuebles, BorderLayout.CENTER);

        JPanel panelBtns = new JPanel(new GridLayout(1,2,8,0));
        panelBtns.setBackground(Color.WHITE);
        panelBtns.add(btnNuevoInmueble);
        panelBtns.add(btnEliminarInmueble);
        panelInmueble.add(panelBtns, BorderLayout.EAST);

        panelEntrada.add(panelInmueble);

        // Listener para eliminar vivienda (se mueve al historial)
        btnEliminarInmueble.addActionListener(ev -> {
            if (inmuebleActual == null) {
                JOptionPane.showMessageDialog(this, "No hay una vivienda seleccionada para eliminar.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "¿Confirma eliminar la vivienda seleccionada? Esto la moverá al historial.", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            boolean ok = usuarioActual.eliminarInmueble(inmuebleActual.getId());
            if (ok) {
                cbInmuebles.removeItem(inmuebleActual);
                inmuebleActual = usuarioActual.getInmuebles().isEmpty() ? null : usuarioActual.getInmuebles().get(0);
                if (inmuebleActual != null) cbInmuebles.setSelectedItem(inmuebleActual);
                cargarTablaDispositivos(modeloDisp);
                actualizarInicio();
                JOptionPane.showMessageDialog(this, "Vivienda eliminada y registrada en historial.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la vivienda.");
            }
        });

        panelEntrada.add(Box.createVerticalStrut(15));

        panelEntrada.add(lblDisp);
        panelEntrada.add(Box.createVerticalStrut(5));
        panelEntrada.add(cbDisp);

        panelEntrada.add(Box.createVerticalStrut(12));

        panelEntrada.add(lblCant);
        panelEntrada.add(Box.createVerticalStrut(5));
        panelEntrada.add(tfCant);

        panelEntrada.add(Box.createVerticalStrut(12));

        panelEntrada.add(lblHoras);
        panelEntrada.add(Box.createVerticalStrut(5));
        panelEntrada.add(tfHoras);

        panelEntrada.add(Box.createVerticalStrut(15));

        panelEntrada.add(panelBotones);

//==============================
// CARGA INICIAL
//==============================

        if (inmuebleActual != null) {

            cargarTablaDispositivos(modeloDisp);

        }

        panel.add(panelEntrada, BorderLayout.NORTH);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;

    }

    private JPanel crearPanelCatalogoDispositivos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayout(2, 3, 10, 10));
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Registrar dispositivo en catálogo"),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel lblNombre = new JLabel("Nombre del dispositivo:");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField tfNombre = new JTextField();
        estilizarTextField(tfNombre);

        JLabel lblPotencia = new JLabel("Potencia (W):");
        lblPotencia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField tfPotencia = new JTextField();
        estilizarTextField(tfPotencia);

        JButton btnAgregarCatalogo = crearBotonPrimario("Registrar dispositivo", -1, 36);

        panelFormulario.add(lblNombre);
        panelFormulario.add(tfNombre);
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(lblPotencia);
        panelFormulario.add(tfPotencia);
        panelFormulario.add(btnAgregarCatalogo);

        String[] columnas = {"ID", "Dispositivo", "Potencia nominal (W)"};
        DefaultTableModel modeloCatalogo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaCatalogo = new JTable(modeloCatalogo);
        estilizarTabla(tablaCatalogo);
        JScrollPane scroll = new JScrollPane(tablaCatalogo);

        Runnable cargarTablaCatalogo = () -> {
            modeloCatalogo.setRowCount(0);
            for (Dispositivo d : catalogo.getCatalogo()) {
                modeloCatalogo.addRow(new Object[]{
                        d.getId(),
                        d.getNombre(),
                        String.format("%.0f", d.getPotencia())
                });
            }
        };
        cargarTablaCatalogo.run();

        btnAgregarCatalogo.addActionListener(e -> {
            try {
                String nombre = tfNombre.getText().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese el nombre del dispositivo.");
                    return;
                }

                double potencia = Double.parseDouble(tfPotencia.getText().trim());
                Dispositivo agregado = registrarDispositivoCatalogoDesdeInterfaz(nombre, potencia);

                cargarTablaCatalogo.run();
                tfNombre.setText("");
                tfPotencia.setText("");

                JOptionPane.showMessageDialog(
                        this,
                        "El dispositivo se registró correctamente en el catálogo con ID: " + agregado.getId()
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, construirMensajeError(ex), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(panelFormulario, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaResumen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createTitledBorder(
                inmuebleActual != null ? "Análisis de la vivienda seleccionada" : "Análisis global del usuario"));

        JButton btnResumen  = crearBotonPrimario("📋 Ver Resumen",  160, 40);
        JButton btnAnalisis = crearBotonPrimario("📊 Ver Análisis", 160, 40);

        JTextPane taResultados = crearAreaInforme();
        JScrollPane scroll = new JScrollPane(taResultados);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        btnResumen.addActionListener(e -> {
            String texto = generarResumenContextual();
            taResultados.setText(formatearInformeHtml(texto, false));
            taResultados.setCaretPosition(0);
        });

        btnAnalisis.addActionListener(e -> {
            String texto = generarAnalisisContextual();
            taResultados.setText(formatearInformeHtml(texto, true));
            taResultados.setCaretPosition(0);
        });

        panelBotones.add(btnResumen);
        panelBotones.add(btnAnalisis);
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaFactura() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setBorder(BorderFactory.createTitledBorder(
                inmuebleActual != null ? "Factura de la vivienda seleccionada" : "Factura global del usuario"));

        JButton btnGenerar = crearBotonPrimario("🧾 Generar Factura", 170, 40);

        JTextPane taFactura = crearAreaInforme();
        JScrollPane scroll = new JScrollPane(taFactura);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        btnGenerar.addActionListener(e -> {
            String texto = generarFacturaContextual();
            taFactura.setText(formatearInformeHtml(texto, false));
            taFactura.setCaretPosition(0);
        });

        panelBoton.add(btnGenerar);
        panel.add(panelBoton, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaRecomendaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setBorder(BorderFactory.createTitledBorder(
                inmuebleActual != null ? "Recomendaciones de la vivienda seleccionada" : "Recomendaciones globales del usuario"));

        JButton btnObtener = crearBotonPrimario("💡 Obtener Recomendaciones", 220, 40);

        JTextPane taRecom = crearAreaInforme();
        JScrollPane scroll = new JScrollPane(taRecom);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        btnObtener.addActionListener(e -> {
            String texto = generarRecomendacionesContextuales();
            String nivel = inmuebleActual != null ? calcularNivelVivienda(inmuebleActual) : sistema.getAnalizador().clasificarConsumo(usuarioActual);
            taRecom.setText(formatearRecomendacionesHtml(texto, nivel));
            taRecom.setCaretPosition(0);
        });

        panelBoton.add(btnObtener);
        panel.add(panelBoton, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaRetos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Retos de ahorro energético"),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel lblReto = new JLabel("Reto semanal: Reducir el consumo un 10%.");
        lblReto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblReto.setForeground(COLOR_TEXTO);

        double baseCalculada = sistema.getCalculadora().calcularConsumoMensual(usuarioActual) / 4.0;
        final double consumoSemanalBase = baseCalculada <= 0 ? 1.0 : baseCalculada;
        double metaSemanal = consumoSemanalBase * 0.90;
        String claveUsuario = usuarioActual.getCedula();
        double consumoActualInicial = consumoSemanalRetoPorUsuario.getOrDefault(claveUsuario, consumoSemanalBase);
        consumoSemanalRetoPorUsuario.put(claveUsuario, consumoActualInicial);

        JLabel lblObjetivo = new JLabel(String.format(
                "Meta semanal: pasar de %.2f kWh a %.2f kWh o menos.",
                consumoSemanalBase, metaSemanal
        ));
        lblObjetivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblObjetivo.setForeground(COLOR_TEXTO_SUAVE);

        JPanel panelEntrada = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelEntrada.setBackground(Color.WHITE);
        JLabel lblConsumoActual = new JLabel("Consumo semanal actual (kWh):");
        lblConsumoActual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField tfConsumoActual = new JTextField(String.format(Locale.US, "%.2f", consumoActualInicial), 12);
        estilizarTextField(tfConsumoActual);
        JButton btnActualizar = crearBotonPrimario("Actualizar progreso", -1, 34);
        JButton btnGuardarNivel = crearBotonExito("Guardar nivel", -1, 34);
        panelEntrada.add(lblConsumoActual);
        panelEntrada.add(tfConsumoActual);
        panelEntrada.add(btnActualizar);
        panelEntrada.add(btnGuardarNivel);

        JLabel lblProgresoTitulo = new JLabel("Progreso");
        lblProgresoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblBarra = new JLabel();
        lblBarra.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBarra.setForeground(COLOR_PRIMARIO);
        JLabel lblNivel = new JLabel();
        lblNivel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNivel.setForeground(COLOR_TEXTO);
        JLabel lblMensajeLogro = new JLabel();
        lblMensajeLogro.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JTextArea areaNiveles = new JTextArea();
        areaNiveles.setEditable(false);
        areaNiveles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaNiveles.setLineWrap(true);
        areaNiveles.setWrapStyleWord(true);
        areaNiveles.setBackground(Color.WHITE);
        areaNiveles.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Medallas / niveles guardados"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        Runnable actualizarVista = () -> {
            double consumoActual;
            try {
                consumoActual = leerDecimalFlexible(tfConsumoActual.getText(), "consumo semanal");
                if (consumoActual < 0) throw new NumberFormatException("El consumo no puede ser negativo.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ingrese un consumo semanal válido en kWh.",
                        "Dato inválido",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            consumoSemanalRetoPorUsuario.put(claveUsuario, consumoActual);
            double reduccion = ((consumoSemanalBase - consumoActual) / consumoSemanalBase) * 100.0;
            if (reduccion < 0) reduccion = 0;
            double progreso = Math.min(100.0, (reduccion / 10.0) * 100.0);

            lblBarra.setText(construirBarraProgresoReto(progreso));
            String nivel = resolverNivelReto(progreso);
            lblNivel.setText("Nivel actual: " + nivel + " | Reducción lograda: " + String.format("%.2f", reduccion) + "%");

            if (progreso >= 100.0) {
                lblMensajeLogro.setText("🏆 ¡Felicidades! Has reducido tu consumo.");
                lblMensajeLogro.setForeground(new Color(27, 124, 36));
            } else {
                lblMensajeLogro.setText("Sigue avanzando: estás construyendo hábitos de consumo eficiente.");
                lblMensajeLogro.setForeground(new Color(21, 101, 192));
            }

            LinkedHashSet<String> niveles = nivelesRetoPorUsuario.computeIfAbsent(claveUsuario, k -> new LinkedHashSet<>());
            if (niveles.isEmpty()) {
                areaNiveles.setText("No hay niveles guardados todavía.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String n : niveles) {
                    sb.append("• ").append(n).append("\n");
                }
                areaNiveles.setText(sb.toString());
            }
        };

        btnActualizar.addActionListener(e -> actualizarVista.run());
        btnGuardarNivel.addActionListener(e -> {
            double consumoActual;
            try {
                consumoActual = leerDecimalFlexible(tfConsumoActual.getText(), "consumo semanal");
                if (consumoActual < 0) throw new NumberFormatException("El consumo no puede ser negativo.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ingrese un consumo semanal válido antes de guardar un nivel.",
                        "Dato inválido",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            double reduccion = ((consumoSemanalBase - consumoActual) / consumoSemanalBase) * 100.0;
            if (reduccion < 0) reduccion = 0;
            double progreso = Math.min(100.0, (reduccion / 10.0) * 100.0);
            String nivel = resolverNivelReto(progreso);
            LinkedHashSet<String> niveles = nivelesRetoPorUsuario.computeIfAbsent(claveUsuario, k -> new LinkedHashSet<>());
            niveles.add(nivel);
            actualizarVista.run();
            JOptionPane.showMessageDialog(this, "Nivel guardado: " + nivel);
        });

        panelSuperior.add(lblReto);
        panelSuperior.add(Box.createVerticalStrut(5));
        panelSuperior.add(lblObjetivo);
        panelSuperior.add(Box.createVerticalStrut(12));
        panelSuperior.add(panelEntrada);
        panelSuperior.add(Box.createVerticalStrut(14));
        panelSuperior.add(lblProgresoTitulo);
        panelSuperior.add(Box.createVerticalStrut(6));
        panelSuperior.add(lblBarra);
        panelSuperior.add(Box.createVerticalStrut(8));
        panelSuperior.add(lblNivel);
        panelSuperior.add(Box.createVerticalStrut(8));
        panelSuperior.add(lblMensajeLogro);

        actualizarVista.run();

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(areaNiveles, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPestanaConsumoGlobal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setBorder(BorderFactory.createTitledBorder("Consumo global del usuario"));

        JButton btnResumen = crearBotonPrimario("Resumen global", 150, 40);
        JButton btnAnalisis = crearBotonPrimario("Análisis global", 150, 40);
        JButton btnFactura = crearBotonPrimario("Factura global", 150, 40);

        JTextPane taGlobal = crearAreaInforme();
        JScrollPane scroll = new JScrollPane(taGlobal);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));

        btnResumen.addActionListener(e -> {
            taGlobal.setText(formatearInformeHtml(sistema.obtenerResumenUsuario(usuarioActual), false));
            taGlobal.setCaretPosition(0);
        });

        btnAnalisis.addActionListener(e -> {
            taGlobal.setText(formatearInformeHtml(sistema.obtenerAnalisisUsuario(usuarioActual), true));
            taGlobal.setCaretPosition(0);
        });

        btnFactura.addActionListener(e -> {
            taGlobal.setText(formatearInformeHtml(sistema.generarFactura(usuarioActual), false));
            taGlobal.setCaretPosition(0);
        });

        panelBoton.add(btnResumen);
        panelBoton.add(btnAnalisis);
        panelBoton.add(btnFactura);
        panel.add(panelBoton, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private String generarResumenContextual() {
        if (inmuebleActual == null) {
            return sistema.obtenerResumenUsuario(usuarioActual);
        }
        return generarResumenVivienda(inmuebleActual);
    }

    private String generarAnalisisContextual() {
        if (inmuebleActual == null) {
            return sistema.obtenerAnalisisUsuario(usuarioActual);
        }
        return generarAnalisisVivienda(inmuebleActual);
    }

    private String generarFacturaContextual() {
        if (inmuebleActual == null) {
            return sistema.generarFactura(usuarioActual);
        }
        return generarFacturaVivienda(inmuebleActual);
    }

    private String generarRecomendacionesContextuales() {
        if (inmuebleActual == null) {
            return sistema.obtenerRecomendaciones(usuarioActual);
        }
        return generarRecomendacionesVivienda(inmuebleActual);
    }

    private String generarResumenVivienda(Inmueble inmueble) {
        StringBuilder texto = new StringBuilder();
        double consumoDiario = calcularConsumoInmuebleDiario(inmueble);
        double consumoMensual = calcularConsumoInmuebleMensual(inmueble);
        double consumoAnual = consumoMensual * 12.0;
        double costoMensual = consumoMensual * usuarioActual.obtenerTarifa();
        int dispositivos = inmueble.getDispositivos().size();
        double potenciaTotal = 0.0;
        for (Dispositivo d : inmueble.getDispositivos()) {
            potenciaTotal += d.getPotencia() * d.getCantidad();
        }

        texto.append("\n===== RESUMEN DE VIVIENDA =====\n");
        texto.append("Vivienda: ").append(inmueble.getNombre()).append("\n");
        texto.append("Tipo: ").append(inmueble.getTipo()).append("\n");
        texto.append("Dispositivos registrados: ").append(dispositivos).append("\n");
        texto.append("Potencia total instalada: ").append(String.format(Locale.US, "%.2f", potenciaTotal)).append(" W\n");
        texto.append(String.format(Locale.US, "Consumo diario: %.2f kWh%n", consumoDiario));
        texto.append(String.format(Locale.US, "Consumo mensual: %.2f kWh%n", consumoMensual));
        texto.append(String.format(Locale.US, "Consumo anual: %.2f kWh%n", consumoAnual));
        texto.append(String.format(Locale.US, "Costo mensual estimado: $%.2f%n", costoMensual));
        return texto.toString();
    }

    private String generarAnalisisVivienda(Inmueble inmueble) {
        StringBuilder texto = new StringBuilder();
        double consumoMensual = calcularConsumoInmuebleMensual(inmueble);
        double limite = usuarioActual.getSector().getLimiteConsumo();
        Dispositivo mayor = obtenerMayorConsumidorInmueble(inmueble);

        texto.append("\n===== ANÁLISIS DE VIVIENDA =====\n");
        texto.append("Vivienda: ").append(inmueble.getNombre()).append("\n");
        texto.append("Sector del usuario: ").append(usuarioActual.getSector()).append("\n");
        texto.append(String.format(Locale.US, "Consumo mensual de la vivienda: %.2f kWh%n", consumoMensual));
        texto.append(String.format(Locale.US, "Límite recomendado del sector: %.2f kWh%n", limite));
        texto.append(String.format(Locale.US, "Diferencia frente al límite: %.2f kWh%n", consumoMensual - limite));
        texto.append("Clasificación estimada: ").append(calcularNivelVivienda(inmueble)).append("\n");

        if (mayor != null) {
            texto.append("\nDispositivo de mayor consumo:\n");
            texto.append("Nombre: ").append(mayor.getNombre()).append("\n");
            texto.append(String.format(Locale.US, "Consumo mensual: %.2f kWh%n", mayor.calcularConsumoMensual()));
        }

        texto.append("\nDetalle por dispositivo:\n");
        for (Dispositivo d : inmueble.getDispositivos()) {
            texto.append("- ").append(d.getNombre())
                    .append(" | Cantidad: ").append(d.getCantidad())
                    .append(" | Horas/día: ").append(String.format(Locale.US, "%.2f", d.getHorasUsoDiarias()))
                    .append(" | Mensual: ").append(String.format(Locale.US, "%.2f", d.calcularConsumoMensual()))
                    .append(" kWh\n");
        }
        if (inmueble.getDispositivos().isEmpty()) {
            texto.append("Sin dispositivos registrados.\n");
        }
        return texto.toString();
    }

    private String generarFacturaVivienda(Inmueble inmueble) {
        StringBuilder texto = new StringBuilder();
        double consumoMensual = calcularConsumoInmuebleMensual(inmueble);
        double tarifa = usuarioActual.obtenerTarifa();
        double subtotal = consumoMensual * tarifa;

        texto.append("\n===== FACTURA DE VIVIENDA =====\n");
        texto.append("Vivienda: ").append(inmueble.getNombre()).append("\n");
        texto.append("Tarifa aplicada: $").append(String.format(Locale.US, "%.2f", tarifa)).append("/kWh\n");
        texto.append("\nDetalle de consumo:\n");
        for (Dispositivo d : inmueble.getDispositivos()) {
            texto.append("- ").append(d.getNombre())
                    .append(" | Consumo mensual: ").append(String.format(Locale.US, "%.2f", d.calcularConsumoMensual()))
                    .append(" kWh | Subtotal: $").append(String.format(Locale.US, "%.2f", d.calcularConsumoMensual() * tarifa))
                    .append("\n");
        }
        if (inmueble.getDispositivos().isEmpty()) {
            texto.append("No hay dispositivos para facturar en esta vivienda.\n");
        }
        texto.append("\nConsumo total vivienda: ").append(String.format(Locale.US, "%.2f", consumoMensual)).append(" kWh\n");
        texto.append("Total estimado: $").append(String.format(Locale.US, "%.2f", subtotal)).append("\n");
        return texto.toString();
    }

    private String generarRecomendacionesVivienda(Inmueble inmueble) {
        StringBuilder texto = new StringBuilder();
        double consumoMensual = calcularConsumoInmuebleMensual(inmueble);
        Dispositivo mayor = obtenerMayorConsumidorInmueble(inmueble);
        texto.append("\n===== RECOMENDACIONES DE VIVIENDA =====\n");
        texto.append("Vivienda: ").append(inmueble.getNombre()).append("\n");
        if (inmueble.getDispositivos().isEmpty()) {
            texto.append("Agregue dispositivos para obtener recomendaciones más precisas.\n");
            return texto.toString();
        }
        if (consumoMensual > usuarioActual.getSector().getLimiteConsumo()) {
            texto.append("El consumo mensual de esta vivienda supera el límite recomendado del sector.\n");
        } else {
            texto.append("El consumo mensual de esta vivienda está dentro de parámetros aceptables.\n");
        }
        if (mayor != null) {
            texto.append("Principal foco de mejora: ").append(mayor.getNombre()).append("\n");
        }
        texto.append("Sugerencias:\n");
        texto.append("- Revisar equipos con muchas horas de uso.\n");
        texto.append("- Reducir cantidades de equipos encendidos simultáneamente.\n");
        texto.append("- Priorizar dispositivos de bajo consumo.\n");
        return texto.toString();
    }

    private double calcularConsumoInmuebleDiario(Inmueble inmueble) {
        double total = 0.0;
        for (Dispositivo d : inmueble.getDispositivos()) {
            total += d.calcularConsumoDiario();
        }
        return total;
    }

    private double calcularConsumoInmuebleMensual(Inmueble inmueble) {
        double total = 0.0;
        for (Dispositivo d : inmueble.getDispositivos()) {
            total += d.calcularConsumoMensual();
        }
        return total;
    }

    private Dispositivo obtenerMayorConsumidorInmueble(Inmueble inmueble) {
        Dispositivo mayor = null;
        double consumoMayor = 0.0;
        for (Dispositivo d : inmueble.getDispositivos()) {
            double consumo = d.calcularConsumoMensual();
            if (consumo > consumoMayor) {
                consumoMayor = consumo;
                mayor = d;
            }
        }
        return mayor;
    }

    private String calcularNivelVivienda(Inmueble inmueble) {
        double consumoMensual = calcularConsumoInmuebleMensual(inmueble);
        double limite = usuarioActual.getSector().getLimiteConsumo();
        if (consumoMensual < limite * 0.50) return "BAJO";
        if (consumoMensual < limite) return "NORMAL";
        if (consumoMensual < limite * 1.30) return "ALTO";
        return "CRITICO";
    }

    private JPanel crearPestanaInicio() {

        panelInicio = new JPanel(new BorderLayout());

        JPanel panel = panelInicio;

        panel.setBackground(COLOR_FONDO);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contenedor = new JPanel();

        contenedor.setLayout(new GridLayout(3, 2, 20, 20));

        contenedor.setBackground(COLOR_FONDO);

        // ==========================
        // TARJETA USUARIO
        // ==========================

        String datosUsuario = "Nombre: " + usuarioActual.getNombre() + "\nCédula: " + usuarioActual.getCedula() + "\nSector: " + usuarioActual.getSector();

        if (inmuebleActual != null) {

            datosUsuario += "\nInmueble: " + inmuebleActual.getNombre() + "\nTipo: " + inmuebleActual.getTipo();

        }

        JPanel tarjetaUsuario = crearTarjeta("USUARIO", datosUsuario);

        // ==========================
        // TARJETA CONSUMO
        // ==========================

        double consumo = sistema.getCalculadora().calcularConsumoMensual(usuarioActual);

        JPanel tarjetaConsumo = crearTarjeta("CONSUMO ENERGÉTICO", String.format("%.2f kWh mensual", consumo));

        // ==========================
        // TARJETA ESTADO
        // ==========================

        String estado = sistema.getAnalizador().clasificarConsumo(usuarioActual);

        JPanel tarjetaEstado = crearTarjeta("ESTADO ENERGÉTICO", estado);

        // ==========================
        // TARJETA DISPOSITIVOS
        // ==========================

        int cantidad = 0;

        if (inmuebleActual != null) {

            cantidad = inmuebleActual.getDispositivos().size();

        }

        JPanel tarjetaDispositivos = crearTarjeta("DISPOSITIVOS", "Registrados: " + cantidad);

        // ==========================
        // TARJETA EMPRESA
        // ==========================

        String extra = "";

        if (usuarioActual instanceof UsuarioEmpresa) {

            UsuarioEmpresa emp = (UsuarioEmpresa) usuarioActual;

            extra = "RUC: " + emp.getRuc() + "\nActividad: " + emp.getActividadEconomica();

        }

        JPanel tarjetaExtra = crearTarjeta("INFORMACIÓN", extra.isEmpty() ? "Usuario residencial" : extra);

        contenedor.add(tarjetaUsuario);
        contenedor.add(tarjetaConsumo);
        contenedor.add(tarjetaEstado);
        contenedor.add(tarjetaDispositivos);
        contenedor.add(tarjetaExtra);

        double consumoDiario = calcularConsumoDiarioKwh(usuarioActual);
        JPanel tarjetaDiario = crearTarjeta("CONSUMO DIARIO",
                String.format("%.3f kWh / día", consumoDiario));
        contenedor.add(tarjetaDiario);

        JScrollPane scrollTarjetas = new JScrollPane(contenedor);
        scrollTarjetas.setBorder(BorderFactory.createEmptyBorder());
        scrollTarjetas.getViewport().setBackground(COLOR_FONDO);

        JPanel panelGraficosInicio = crearPanelGraficosUsuario(usuarioActual, "Visualización general de consumo");
        JSplitPane splitInicio = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTarjetas, panelGraficosInicio);
        splitInicio.setResizeWeight(0.64);
        splitInicio.setBorder(BorderFactory.createEmptyBorder());
        splitInicio.setDividerSize(8);
        panel.add(splitInicio, BorderLayout.CENTER);

        return panel;

    }

    private JPanel crearTarjeta(String titulo, String contenido) {

        Color acento = obtenerColorTarjeta(titulo);

        // Wrapper que simula sombra
        JPanel sombra = new JPanel(new BorderLayout());
        sombra.setBackground(new Color(200, 210, 230));
        sombra.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 3));

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, acento),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(225, 232, 248), 1),
                        BorderFactory.createEmptyBorder(18, 18, 18, 18))));

        // Encabezado: icono + título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelTitulo.setBackground(Color.WHITE);

        JLabel encabezado = new JLabel(titulo);
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        encabezado.setForeground(acento);

        panelTitulo.add(encabezado);

        // Separador fino bajo el título
        JPanel sep = new JPanel();
        sep.setBackground(new Color(230, 235, 248));
        sep.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JTextArea texto = new JTextArea(contenido);
        texto.setEditable(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        texto.setForeground(COLOR_TEXTO);
        texto.setBackground(Color.WHITE);
        texto.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        if (titulo.equals("ESTADO ENERGÉTICO")) {
            texto.setFont(new Font("Segoe UI", Font.BOLD, 26));
            texto.setAlignmentX(Component.CENTER_ALIGNMENT);
            switch (contenido) {
                case "BAJO":    texto.setForeground(new Color(27, 124, 36));  break;
                case "NORMAL":  texto.setForeground(new Color(21, 101, 192)); break;
                case "ALTO":    texto.setForeground(new Color(210, 100,  0)); break;
                case "CRITICO": texto.setForeground(new Color(198,  40, 40)); break;
            }
        }

        tarjeta.add(panelTitulo, BorderLayout.NORTH);
        tarjeta.add(texto, BorderLayout.CENTER);
        sombra.add(tarjeta, BorderLayout.CENTER);
        return sombra;
    }

    private Color obtenerColorTarjeta(String titulo) {
        switch (titulo) {
            case "CONSUMO ENERGÉTICO": return new Color(21,  101, 192);
            case "ESTADO ENERGÉTICO":  return new Color(46,  125,  50);
            case "DISPOSITIVOS":       return new Color(230, 111,   0);
            case "USUARIO":            return new Color(0,   131, 143);
            case "INFORMACIÓN":        return new Color(123,  31, 162);
            case "CONSUMO DIARIO":     return new Color(21,  101, 192);
            default:                   return COLOR_PRIMARIO;
        }
    }

    private JPanel crearPanelMarca() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(6, 14, 46),
                                                     getWidth(), 0, COLOR_BARRA_SUPERIOR);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel lblIcono = new JLabel("⚡ ");
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        lblIcono.setForeground(new Color(100, 180, 255));

        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);

        JLabel lblSistema = new JLabel("Sistema de Monitoreo de Consumo Eléctrico");
        lblSistema.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblSistema.setForeground(Color.WHITE);

        JLabel lblMarca = new JLabel("Inteligencia Eléctrica  ·  Gestión Energética Eficiente");
        lblMarca.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMarca.setForeground(new Color(180, 205, 240));

        panelTexto.add(lblSistema);
        panelTexto.add(Box.createVerticalStrut(2));
        panelTexto.add(lblMarca);

        panel.add(lblIcono);
        panel.add(panelTexto);
        return panel;
    }

    private void cargarCatalogo(JComboBox<String> cb) {
        cb.removeAllItems();
        cb.addItem("Seleccione un dispositivo");
        for (Dispositivo d : catalogo.getCatalogo()) {
            cb.addItem(String.format("ID: %d - %s (%.0f W)", d.getId(), d.getNombre(), d.getPotencia()));
        }
    }

    private Dispositivo registrarDispositivoCatalogoDesdeInterfaz(String nombre, double potencia) throws DatoInvalidoException {
        String nombreLimpio = nombre == null ? "" : nombre.trim();
        if (nombreLimpio.isEmpty()) {
            throw new DatoInvalidoException("El nombre del dispositivo no puede estar vacío");
        }
        for (Dispositivo d : catalogo.getCatalogo()) {
            if (d.getNombre().equalsIgnoreCase(nombreLimpio)) {
                throw new DatoInvalidoException("Ya existe un dispositivo con ese nombre en el catálogo");
            }
        }
        int siguienteId = 1;
        for (Dispositivo d : catalogo.getCatalogo()) {
            if (d.getId() >= siguienteId) {
                siguienteId = d.getId() + 1;
            }
        }
        Dispositivo nuevo = new Dispositivo(siguienteId, nombreLimpio, potencia, 1, 1);
        catalogo.getCatalogo().add(nuevo);
        return nuevo;
    }

    private int obtenerIdDesdeItem(String item) {
        int inicio = item.indexOf("ID:");
        int separador = item.indexOf(" - ");
        if (inicio != 0 || separador < 0) {
            throw new NumberFormatException("Formato de dispositivo invalido");
        }
        return Integer.parseInt(item.substring(3, separador).trim());
    }

    private int generarIdTemporal() {

        int mayor = 999;

        if (usuarioActual == null) {

            return 1000;

        }

        for (Inmueble inmueble : usuarioActual.getInmuebles()) {

            for (Dispositivo dispositivo : inmueble.getDispositivos()) {

                if (dispositivo.getId() > mayor) {

                    mayor = dispositivo.getId();

                }

            }

        }

        return mayor + 1;

    }

    private double leerDecimalFlexible(String texto, String campo) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            throw new NumberFormatException("Ingrese " + campo + " válido en kWh.");
        }
        limpio = limpio.replace(',', '.');
        return Double.parseDouble(limpio);
    }

    private int generarIdInmueble() {

        int mayor = 0;

        for (Usuario u : sistema.getGestorUsuarios().getUsuarios()) {

            for (Inmueble i : u.getInmuebles()) {

                if (i.getId() > mayor) {

                    mayor = i.getId();

                }

            }

        }

        return mayor + 1;

    }

    private void estilizarTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(Color.WHITE);
        tf.setForeground(COLOR_TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }

    private class PanelConFondoPCB extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(6, 125, 233, 20));
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int spacing = 50;
            for (int i = 0; i < getWidth(); i += spacing) {
                g2.drawLine(i, 0, i, getHeight());
            }
            for (int i = 0; i < getHeight(); i += spacing) {
                g2.drawLine(0, i, getWidth(), i);
            }

            for (int i = spacing; i < getWidth(); i += spacing * 2) {
                for (int j = spacing; j < getHeight(); j += spacing * 2) {
                    g2.fillOval(i - 2, j - 2, 4, 4);
                }
            }

            g2.dispose();
        }
    }

    // ─── Helpers visuales ────────────────────────────────────────────────────

    /** Estiliza un JTable con cabecera coloreada, filas alternas y sin líneas verticales. */
    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(30);
        tabla.setFont(FONT_TABLA);
        tabla.setGridColor(new Color(230, 235, 248));
        tabla.setSelectionBackground(COLOR_SELECCION);
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(COLOR_ENCABEZADO_TABLA);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_FILA_PAR);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    /** Crea un JButton con estilo primario (azul). */
    private JButton crearBotonPrimario(String texto, int ancho, int alto) {
        return crearBoton(texto, COLOR_PRIMARIO, Color.WHITE, ancho, alto);
    }

    /** Crea un JButton con estilo secundario (contorno azul). */
    private JButton crearBotonSecundario(String texto, int ancho, int alto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_NORMAL);
        btn.setBackground(Color.WHITE);
        btn.setForeground(COLOR_PRIMARIO);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 1));
        if (ancho > 0) btn.setMaximumSize(new Dimension(ancho, alto));
        else            btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, alto));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    /** Crea un JButton con estilo peligro (rojo). */
    private JButton crearBotonPeligro(String texto, int ancho, int alto) {
        return crearBoton(texto, COLOR_PELIGRO, Color.WHITE, ancho, alto);
    }

    /** Crea un JButton con estilo advertencia (ámbar). */
    private JButton crearBotonAdvertencia(String texto, int ancho, int alto) {
        return crearBoton(texto, new Color(245, 127, 23), Color.WHITE, ancho, alto);
    }

    /** Crea un JButton con estilo éxito (verde). */
    private JButton crearBotonExito(String texto, int ancho, int alto) {
        return crearBoton(texto, COLOR_SECUNDARIO, Color.WHITE, ancho, alto);
    }

    private JButton crearBoton(String texto, Color fondo, Color textoColor, int ancho, int alto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(fondo);
        btn.setForeground(textoColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (ancho > 0 && ancho != Integer.MAX_VALUE) btn.setPreferredSize(new Dimension(ancho, alto));
        else if (ancho == Integer.MAX_VALUE)          btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        else                                          btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 16, alto));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    /** Crea una etiqueta de formulario estilizada. */
    private JLabel crearEtiquetaFormulario(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private static String construirMensajeError(Throwable ex) {
        if (ex == null) {
            return "Se produjo un error no identificado.";
        }
        String mensaje = ex.getMessage();
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return "Se produjo un error inesperado (" + ex.getClass().getSimpleName() + ").";
        }
        return mensaje;
    }

    private String construirBarraProgresoReto(double progreso) {
        int porcentaje = (int) Math.round(Math.max(0, Math.min(100, progreso)));
        int llenos = porcentaje / 10;
        StringBuilder barra = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            barra.append(i < llenos ? "█" : "░");
        }
        return barra + " " + porcentaje + "%";
    }

    private String resolverNivelReto(double progreso) {
        if (progreso >= 100) return "Nivel Experto";
        if (progreso >= 70) return "Nivel Eco";
        return "Nivel Verde";
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((hilo, ex) -> SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                        null,
                        construirMensajeError(ex),
                        "Error inesperado",
                        JOptionPane.ERROR_MESSAGE
                )));
        SwingUtilities.invokeLater(MainInterfaz::new);
    }

    private JTextPane crearAreaInforme() {
        JTextPane area = new JTextPane();
        area.setContentType("text/html");
        area.setEditable(false);
        area.setBackground(Color.WHITE);
        area.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return area;
    }

    private String escaparHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private Sector obtenerSectorDesdeTexto(String valor) {
        if (valor == null || valor.equalsIgnoreCase("TODOS")) {
            return null;
        }
        return Sector.valueOf(valor);
    }

    private Double leerDoubleOpcional(String texto, String campo) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) return null;
        double valor = Double.parseDouble(limpio);
        if (valor < 0) {
            throw new NumberFormatException(campo + " no puede ser negativo.");
        }
        return valor;
    }

    private Integer leerEnteroOpcional(String texto, String campo) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) return null;
        int valor = Integer.parseInt(limpio);
        if (valor < 0) {
            throw new NumberFormatException(campo + " no puede ser negativo.");
        }
        return valor;
    }

    private int contarUnidadesDispositivos(Usuario usuario) {
        int total = 0;
        for (Inmueble inmueble : usuario.getInmuebles()) {
            for (Dispositivo dispositivo : inmueble.getDispositivos()) {
                total += dispositivo.getCantidad();
            }
        }
        return total;
    }

    private double calcularConsumoMensualInmueble(Inmueble inmueble) {
        double total = 0;
        for (Dispositivo dispositivo : inmueble.getDispositivos()) {
            total += dispositivo.calcularConsumoMensual();
        }
        return total;
    }

    private List<Usuario> filtrarUsuariosReportes(
            Sector sectorFiltro,
            String tipoCuentaFiltro,
            String nivelFiltro,
            Double consumoMinimo,
            Double consumoMaximo,
            Integer minimoInmuebles,
            Integer minimoDispositivos
    ) {
        List<Usuario> filtrados = new ArrayList<>();
        for (Usuario usuario : sistema.getGestorUsuarios().getUsuarios()) {
            if (usuario.getRol() == Rol.ADMINISTRADOR) continue;

            if (sectorFiltro != null && usuario.getSector() != sectorFiltro) continue;

            String tipoCuenta = usuario instanceof UsuarioEmpresa ? "EMPRESA" : "RESIDENCIAL";
            if (tipoCuentaFiltro != null && !tipoCuentaFiltro.equalsIgnoreCase("TODOS")
                    && !tipoCuenta.equalsIgnoreCase(tipoCuentaFiltro)) {
                continue;
            }

            String nivel = sistema.getAnalizador().clasificarConsumo(usuario);
            if (nivelFiltro != null && !nivelFiltro.equalsIgnoreCase("TODOS")
                    && !nivel.equalsIgnoreCase(nivelFiltro)) {
                continue;
            }

            double consumoMensual = sistema.getCalculadora().calcularConsumoMensual(usuario);
            if (consumoMinimo != null && consumoMensual < consumoMinimo) continue;
            if (consumoMaximo != null && consumoMensual > consumoMaximo) continue;

            int inmuebles = usuario.getInmuebles().size();
            if (minimoInmuebles != null && inmuebles < minimoInmuebles) continue;

            int dispositivos = sistema.getCalculadora().contarDispositivos(usuario);
            if (minimoDispositivos != null && dispositivos < minimoDispositivos) continue;

            filtrados.add(usuario);
        }
        return filtrados;
    }

    private String generarReporteUsuariosDetalladoFiltrado(
            List<Usuario> usuarios,
            Sector sectorFiltro,
            String tipoCuentaFiltro,
            String nivelFiltro,
            Double consumoMinimo,
            Double consumoMaximo,
            Integer minimoInmuebles,
            Integer minimoDispositivos
    ) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("ANÁLISIS DETALLADO DE USUARIOS FILTRADOS\n");
        reporte.append("========================================\n");
        reporte.append("Sector: ").append(sectorFiltro == null ? "TODOS" : sectorFiltro).append("\n");
        reporte.append("Tipo de cuenta: ").append(tipoCuentaFiltro == null ? "TODOS" : tipoCuentaFiltro).append("\n");
        reporte.append("Nivel de consumo: ").append(nivelFiltro == null ? "TODOS" : nivelFiltro).append("\n");
        reporte.append("Consumo mínimo (kWh/mes): ").append(consumoMinimo == null ? "No aplica" : String.format("%.2f", consumoMinimo)).append("\n");
        reporte.append("Consumo máximo (kWh/mes): ").append(consumoMaximo == null ? "No aplica" : String.format("%.2f", consumoMaximo)).append("\n");
        reporte.append("Mínimo de inmuebles: ").append(minimoInmuebles == null ? "No aplica" : minimoInmuebles).append("\n");
        reporte.append("Mínimo de dispositivos: ").append(minimoDispositivos == null ? "No aplica" : minimoDispositivos).append("\n\n");

        if (usuarios.isEmpty()) {
            reporte.append("No existen usuarios que cumplan con los filtros seleccionados.");
            return reporte.toString();
        }

        for (Usuario usuario : usuarios) {
            double consumoDiario = sistema.getCalculadora().calcularConsumoDiario(usuario);
            double consumoMensual = sistema.getCalculadora().calcularConsumoMensual(usuario);
            double consumoAnual = sistema.getCalculadora().calcularConsumoAnual(usuario);
            double costoMensual = sistema.getCalculadora().calcularCostoMensual(usuario);
            String nivel = sistema.getAnalizador().clasificarConsumo(usuario);
            int inmuebles = usuario.getInmuebles().size();
            int dispositivosRegistrados = sistema.getCalculadora().contarDispositivos(usuario);
            int dispositivosUnidades = contarUnidadesDispositivos(usuario);

            reporte.append("USUARIO: ").append(usuario.getNombre()).append("\n");
            reporte.append("Cédula: ").append(usuario.getCedula()).append("\n");
            reporte.append("Tipo de cuenta: ").append(usuario instanceof UsuarioEmpresa ? "Empresa" : "Residencial").append("\n");
            reporte.append("Sector: ").append(usuario.getSector()).append("\n");
            reporte.append("Estado de la cuenta: ").append(usuario.isActivo() ? "Activa" : "Inactiva").append("\n");
            reporte.append("Nivel de consumo: ").append(nivel).append("\n");
            reporte.append("Inmuebles registrados: ").append(inmuebles).append("\n");
            reporte.append("Dispositivos registrados: ").append(dispositivosRegistrados).append("\n");
            reporte.append("Unidades de dispositivos: ").append(dispositivosUnidades).append("\n");
            reporte.append("Consumo diario: ").append(String.format("%.2f", consumoDiario)).append(" kWh\n");
            reporte.append("Consumo mensual: ").append(String.format("%.2f", consumoMensual)).append(" kWh\n");
            reporte.append("Consumo anual: ").append(String.format("%.2f", consumoAnual)).append(" kWh\n");
            reporte.append("Costo mensual estimado: $").append(String.format("%.2f", costoMensual)).append("\n");

            double limiteSector = usuario.getSector().getLimiteConsumo();
            reporte.append("Límite recomendado por sector: ").append(String.format("%.2f", limiteSector)).append(" kWh/mes\n");
            reporte.append("Variación frente al límite: ").append(String.format("%.2f", consumoMensual - limiteSector)).append(" kWh\n");

            Inmueble inmuebleMayor = null;
            double mayorConsumoInmueble = 0;
            for (Inmueble inmueble : usuario.getInmuebles()) {
                double consumoInmueble = calcularConsumoMensualInmueble(inmueble);
                if (consumoInmueble > mayorConsumoInmueble) {
                    mayorConsumoInmueble = consumoInmueble;
                    inmuebleMayor = inmueble;
                }
            }

            if (inmuebleMayor != null) {
                reporte.append("Inmueble de mayor consumo: ").append(inmuebleMayor.getNombre())
                        .append(" (").append(String.format("%.2f", mayorConsumoInmueble)).append(" kWh/mes)\n");
            }

            Dispositivo dispositivoMayor = sistema.getAnalizador().obtenerMayorConsumidor(usuario);
            if (dispositivoMayor != null) {
                reporte.append("Dispositivo de mayor impacto: ").append(dispositivoMayor.getNombre())
                        .append(" (").append(String.format("%.2f", dispositivoMayor.calcularConsumoMensual())).append(" kWh/mes)\n");
            }

            reporte.append("Detalle por inmueble y dispositivo:\n");
            for (Inmueble inmueble : usuario.getInmuebles()) {
                double consumoInmueble = calcularConsumoMensualInmueble(inmueble);
                reporte.append("- Inmueble: ").append(inmueble.getNombre())
                        .append(" | Tipo: ").append(inmueble.getTipo())
                        .append(" | Consumo mensual: ").append(String.format("%.2f", consumoInmueble)).append(" kWh\n");
                if (inmueble.getDispositivos().isEmpty()) {
                    reporte.append("  - Sin dispositivos registrados.\n");
                    continue;
                }
                for (Dispositivo dispositivo : inmueble.getDispositivos()) {
                    reporte.append("  - Dispositivo: ").append(dispositivo.getNombre())
                            .append(" | Potencia: ").append(String.format("%.0f", dispositivo.getPotencia())).append(" W")
                            .append(" | Cantidad: ").append(dispositivo.getCantidad())
                            .append(" | Horas/día: ").append(String.format("%.2f", dispositivo.getHorasUsoDiarias()))
                            .append(" | Consumo diario: ").append(String.format("%.2f", dispositivo.calcularConsumoDiario())).append(" kWh")
                            .append(" | Consumo mensual: ").append(String.format("%.2f", dispositivo.calcularConsumoMensual())).append(" kWh\n");
                }
            }

            // Incluir historial de inmuebles eliminados en el reporte (si existe)
            if (usuario.getHistorialInmuebles() != null && !usuario.getHistorialInmuebles().isEmpty()) {
                reporte.append("\nHistorial de inmuebles (eliminados):\n");
                for (Inmueble inmueble : usuario.getHistorialInmuebles()) {
                    double consumoInmueble = calcularConsumoMensualInmueble(inmueble);
                    reporte.append("- Inmueble: ").append(inmueble.getNombre()).append(" (Histórico)")
                            .append(" | Tipo: ").append(inmueble.getTipo())
                            .append(" | Consumo mensual: ").append(String.format("%.2f", consumoInmueble)).append(" kWh\n");
                    if (inmueble.getDispositivos().isEmpty()) {
                        reporte.append("  - Sin dispositivos registrados.\n");
                        continue;
                    }
                    for (Dispositivo dispositivo : inmueble.getDispositivos()) {
                        reporte.append("  - Dispositivo: ").append(dispositivo.getNombre())
                                .append(" | Potencia: ").append(String.format("%.0f", dispositivo.getPotencia())).append(" W")
                                .append(" | Cantidad: ").append(dispositivo.getCantidad())
                                .append(" | Horas/día: ").append(String.format("%.2f", dispositivo.getHorasUsoDiarias()))
                                .append(" | Consumo diario: ").append(String.format("%.2f", dispositivo.calcularConsumoDiario())).append(" kWh")
                                .append(" | Consumo mensual: ").append(String.format("%.2f", dispositivo.calcularConsumoMensual())).append(" kWh\n");
                    }
                }
            }

            reporte.append("------------------------------------------------------------\n");
        }
        return reporte.toString();
    }

    private String generarAnalisisDetalladoUsuario(Usuario usuario) {
        List<Usuario> lista = new ArrayList<>();
        lista.add(usuario);
        return generarReporteUsuariosDetalladoFiltrado(
                lista,
                null,
                usuario instanceof UsuarioEmpresa ? "EMPRESA" : "RESIDENCIAL",
                sistema.getAnalizador().clasificarConsumo(usuario),
                null,
                null,
                null,
                null
        );
    }

    private String generarTopConsumoUsuarios(List<Usuario> usuarios, int limite) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("RANKING DE USUARIOS CON MAYOR CONSUMO\n");
        reporte.append("=====================================\n");

        if (usuarios.isEmpty()) {
            reporte.append("No existen usuarios disponibles para construir el ranking.");
            return reporte.toString();
        }

        usuarios.sort((a, b) -> Double.compare(
                sistema.getCalculadora().calcularConsumoMensual(b),
                sistema.getCalculadora().calcularConsumoMensual(a)
        ));

        int top = Math.min(limite, usuarios.size());
        for (int i = 0; i < top; i++) {
            Usuario usuario = usuarios.get(i);
            double consumo = sistema.getCalculadora().calcularConsumoMensual(usuario);
            double costo = sistema.getCalculadora().calcularCostoMensual(usuario);
            reporte.append("Posición ").append(i + 1).append(": ").append(usuario.getNombre()).append("\n");
            reporte.append("Cédula: ").append(usuario.getCedula()).append("\n");
            reporte.append("Sector: ").append(usuario.getSector()).append("\n");
            reporte.append("Consumo mensual: ").append(String.format("%.2f", consumo)).append(" kWh\n");
            reporte.append("Costo mensual estimado: $").append(String.format("%.2f", costo)).append("\n");
            reporte.append("Nivel de consumo: ").append(sistema.getAnalizador().clasificarConsumo(usuario)).append("\n");
            reporte.append("----------------------------------------\n");
        }

        return reporte.toString();
    }

    private String generarAnalisisGlobalUsuarios(List<Usuario> usuarios) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("ANÁLISIS GLOBAL DE USUARIOS\n");
        reporte.append("===========================\n");

        if (usuarios.isEmpty()) {
            reporte.append("No existen usuarios para generar el análisis global.");
            return reporte.toString();
        }

        int totalUsuarios = usuarios.size();
        int totalInmuebles = 0;
        int totalDispositivos = 0;
        int totalUnidades = 0;
        int residenciales = 0;
        int empresas = 0;
        double consumoTotal = 0;
        double costoTotal = 0;
        Usuario mayorConsumidor = null;
        Usuario menorConsumidor = null;
        double mayor = Double.NEGATIVE_INFINITY;
        double menor = Double.POSITIVE_INFINITY;

        Map<String, Integer> distribucionNivel = new HashMap<>();
        Map<Sector, Double> consumoPorSector = new HashMap<>();
        Map<Sector, Integer> usuariosPorSector = new HashMap<>();

        for (Usuario usuario : usuarios) {
            double consumoMensual = sistema.getCalculadora().calcularConsumoMensual(usuario);
            double costoMensual = sistema.getCalculadora().calcularCostoMensual(usuario);
            int inmuebles = usuario.getInmuebles().size();
            int dispositivos = sistema.getCalculadora().contarDispositivos(usuario);
            int unidades = contarUnidadesDispositivos(usuario);
            String nivel = sistema.getAnalizador().clasificarConsumo(usuario);

            consumoTotal += consumoMensual;
            costoTotal += costoMensual;
            totalInmuebles += inmuebles;
            totalDispositivos += dispositivos;
            totalUnidades += unidades;

            if (usuario instanceof UsuarioEmpresa) empresas++; else residenciales++;
            distribucionNivel.put(nivel, distribucionNivel.getOrDefault(nivel, 0) + 1);
            consumoPorSector.put(usuario.getSector(), consumoPorSector.getOrDefault(usuario.getSector(), 0.0) + consumoMensual);
            usuariosPorSector.put(usuario.getSector(), usuariosPorSector.getOrDefault(usuario.getSector(), 0) + 1);

            if (consumoMensual > mayor) {
                mayor = consumoMensual;
                mayorConsumidor = usuario;
            }
            if (consumoMensual < menor) {
                menor = consumoMensual;
                menorConsumidor = usuario;
            }
        }

        reporte.append("Usuarios analizados: ").append(totalUsuarios).append("\n");
        reporte.append("Usuarios residenciales: ").append(residenciales).append("\n");
        reporte.append("Usuarios empresariales: ").append(empresas).append("\n");
        reporte.append("Inmuebles registrados: ").append(totalInmuebles).append("\n");
        reporte.append("Dispositivos registrados: ").append(totalDispositivos).append("\n");
        reporte.append("Unidades de dispositivos: ").append(totalUnidades).append("\n");
        reporte.append("Consumo mensual total: ").append(String.format("%.2f", consumoTotal)).append(" kWh\n");
        reporte.append("Consumo mensual promedio por usuario: ").append(String.format("%.2f", consumoTotal / totalUsuarios)).append(" kWh\n");
        reporte.append("Costo mensual total estimado: $").append(String.format("%.2f", costoTotal)).append("\n");
        reporte.append("Costo mensual promedio por usuario: $").append(String.format("%.2f", costoTotal / totalUsuarios)).append("\n");

        if (mayorConsumidor != null) {
            reporte.append("Usuario de mayor consumo: ").append(mayorConsumidor.getNombre())
                    .append(" (").append(String.format("%.2f", mayor)).append(" kWh/mes)\n");
        }
        if (menorConsumidor != null) {
            reporte.append("Usuario de menor consumo: ").append(menorConsumidor.getNombre())
                    .append(" (").append(String.format("%.2f", menor)).append(" kWh/mes)\n");
        }

        reporte.append("Distribución por nivel de consumo:\n");
        for (String nivel : new String[]{"BAJO", "NORMAL", "ALTO", "CRITICO"}) {
            reporte.append("- ").append(nivel).append(": ").append(distribucionNivel.getOrDefault(nivel, 0)).append(" usuarios\n");
        }

        reporte.append("Análisis por sector:\n");
        for (Sector sector : Sector.values()) {
            int cantidadUsuarios = usuariosPorSector.getOrDefault(sector, 0);
            if (cantidadUsuarios == 0) continue;
            double consumoSector = consumoPorSector.getOrDefault(sector, 0.0);
            reporte.append("- Sector ").append(sector).append(": ")
                    .append(cantidadUsuarios).append(" usuarios, ")
                    .append(String.format("%.2f", consumoSector)).append(" kWh/mes, promedio ")
                    .append(String.format("%.2f", consumoSector / cantidadUsuarios)).append(" kWh/mes\n");
        }

        return reporte.toString();
    }

    private String generarAnalisisDispositivosGlobal(List<Usuario> usuarios) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("ANÁLISIS GLOBAL DE USO DE DISPOSITIVOS\n");
        reporte.append("======================================\n");

        if (usuarios.isEmpty()) {
            reporte.append("No existen usuarios para analizar dispositivos.");
            return reporte.toString();
        }

        Map<String, Integer> unidadesPorDispositivo = new HashMap<>();
        Map<String, Double> consumoPorDispositivo = new HashMap<>();
        Map<String, Integer> aparicionesPorDispositivo = new HashMap<>();

        for (Usuario usuario : usuarios) {
            for (Inmueble inmueble : usuario.getInmuebles()) {
                for (Dispositivo dispositivo : inmueble.getDispositivos()) {
                    String nombre = dispositivo.getNombre();
                    unidadesPorDispositivo.put(nombre, unidadesPorDispositivo.getOrDefault(nombre, 0) + dispositivo.getCantidad());
                    consumoPorDispositivo.put(nombre, consumoPorDispositivo.getOrDefault(nombre, 0.0) + dispositivo.calcularConsumoMensual());
                    aparicionesPorDispositivo.put(nombre, aparicionesPorDispositivo.getOrDefault(nombre, 0) + 1);
                }
            }
        }

        if (unidadesPorDispositivo.isEmpty()) {
            reporte.append("No hay dispositivos registrados para los usuarios filtrados.");
            return reporte.toString();
        }

        List<Map.Entry<String, Integer>> rankingUnidades = new ArrayList<>(unidadesPorDispositivo.entrySet());
        rankingUnidades.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<Map.Entry<String, Double>> rankingConsumo = new ArrayList<>(consumoPorDispositivo.entrySet());
        rankingConsumo.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        reporte.append("Top dispositivos por unidades registradas:\n");
        int limiteUnidades = Math.min(8, rankingUnidades.size());
        for (int i = 0; i < limiteUnidades; i++) {
            Map.Entry<String, Integer> item = rankingUnidades.get(i);
            reporte.append("- ").append(item.getKey()).append(": ")
                    .append(item.getValue()).append(" unidades (")
                    .append(aparicionesPorDispositivo.get(item.getKey())).append(" registros)\n");
        }

        reporte.append("Top dispositivos por consumo mensual agregado:\n");
        int limiteConsumo = Math.min(8, rankingConsumo.size());
        for (int i = 0; i < limiteConsumo; i++) {
            Map.Entry<String, Double> item = rankingConsumo.get(i);
            reporte.append("- ").append(item.getKey()).append(": ")
                    .append(String.format("%.2f", item.getValue())).append(" kWh/mes\n");
        }

        double consumoTotal = 0;
        for (double valor : consumoPorDispositivo.values()) {
            consumoTotal += valor;
        }
        reporte.append("Consumo mensual agregado por dispositivos: ").append(String.format("%.2f", consumoTotal)).append(" kWh\n");
        reporte.append("Cantidad de tipos de dispositivos detectados: ").append(unidadesPorDispositivo.size()).append("\n");

        return reporte.toString();
    }

    private List<Usuario> obtenerUsuariosNoAdmin() {
        List<Usuario> usuarios = new ArrayList<>();
        for (Usuario usuario : sistema.getGestorUsuarios().getUsuarios()) {
            if (usuario.getRol() != Rol.ADMINISTRADOR) {
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    private JPanel crearPanelGraficosUsuario(Usuario usuario, String tituloPanel) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(tituloPanel));
        panel.setPreferredSize(new Dimension(0, 260));

        if (usuario == null) {
            panel.add(crearPanelMensajeGrafico("Sin datos de usuario para graficar."));
            panel.add(crearPanelMensajeGrafico("Sin datos de usuario para graficar."));
            return panel;
        }

        double consumoDiario = sistema.getCalculadora().calcularConsumoDiario(usuario);
        double consumoMensual = sistema.getCalculadora().calcularConsumoMensual(usuario);
        double consumoAnual = sistema.getCalculadora().calcularConsumoAnual(usuario);
        panel.add(crearGraficoBarras(
                "Consumo energético (kWh)",
                new String[]{"Diario", "Mensual", "Anual"},
                new double[]{consumoDiario, consumoMensual, consumoAnual}
        ));

        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        for (Inmueble inmueble : usuario.getInmuebles()) {
            double consumoInmueble = calcularConsumoMensualInmueble(inmueble);
            if (consumoInmueble > 0) {
                etiquetas.add(inmueble.getNombre());
                valores.add(consumoInmueble);
            }
        }
        if (valores.isEmpty()) {
            panel.add(crearPanelMensajeGrafico("No hay consumo registrado por inmueble."));
        } else {
            panel.add(crearGraficoPastel(
                    "Distribución de consumo por inmueble (kWh/mes)",
                    etiquetas.toArray(new String[0]),
                    valores.stream().mapToDouble(Double::doubleValue).toArray()
            ));
        }

        return panel;
    }

    private void actualizarGraficosReportesAdmin(JPanel panelDestino, List<Usuario> usuarios, String modo) {
        panelDestino.removeAll();
        if (usuarios == null || usuarios.isEmpty()) {
            panelDestino.add(crearPanelMensajeGrafico("No hay datos para mostrar con los filtros actuales."));
            panelDestino.add(crearPanelMensajeGrafico("No hay datos para mostrar con los filtros actuales."));
            panelDestino.revalidate();
            panelDestino.repaint();
            return;
        }

        if ("DISPOSITIVOS".equals(modo)) {
            panelDestino.add(crearGraficoBarrasTopDispositivos(usuarios));
            panelDestino.add(crearGraficoPastelNivelesConsumo(usuarios));
        } else if ("TOP".equals(modo)) {
            panelDestino.add(crearGraficoBarrasTopUsuarios(usuarios, 5));
            panelDestino.add(crearGraficoPastelSectores(usuarios));
        } else if ("USUARIO".equals(modo)) {
            panelDestino.add(crearGraficoBarrasTopUsuarios(usuarios, 1));
            panelDestino.add(crearGraficoPastelNivelesConsumo(usuarios));
        } else {
            panelDestino.add(crearGraficoBarrasConsumoPorSector(usuarios));
            panelDestino.add(crearGraficoPastelNivelesConsumo(usuarios));
        }

        panelDestino.revalidate();
        panelDestino.repaint();
    }

    private JPanel crearGraficoBarrasConsumoPorSector(List<Usuario> usuarios) {
        Map<Sector, Double> consumoPorSector = new HashMap<>();
        for (Usuario usuario : usuarios) {
            double consumo = sistema.getCalculadora().calcularConsumoMensual(usuario);
            consumoPorSector.put(usuario.getSector(), consumoPorSector.getOrDefault(usuario.getSector(), 0.0) + consumo);
        }

        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        for (Sector sector : Sector.values()) {
            double valor = consumoPorSector.getOrDefault(sector, 0.0);
            if (valor > 0) {
                etiquetas.add(sector.name());
                valores.add(valor);
            }
        }
        if (valores.isEmpty()) {
            return crearPanelMensajeGrafico("No existe consumo para construir gráfico por sector.");
        }
        return crearGraficoBarras(
                "Consumo mensual por sector (kWh)",
                etiquetas.toArray(new String[0]),
                valores.stream().mapToDouble(Double::doubleValue).toArray()
        );
    }

    private JPanel crearGraficoBarrasTopUsuarios(List<Usuario> usuarios, int limite) {
        List<Usuario> copia = new ArrayList<>(usuarios);
        copia.sort((a, b) -> Double.compare(
                sistema.getCalculadora().calcularConsumoMensual(b),
                sistema.getCalculadora().calcularConsumoMensual(a)
        ));
        int top = Math.min(limite, copia.size());
        String[] etiquetas = new String[top];
        double[] valores = new double[top];
        for (int i = 0; i < top; i++) {
            Usuario usuario = copia.get(i);
            etiquetas[i] = usuario.getNombre();
            valores[i] = sistema.getCalculadora().calcularConsumoMensual(usuario);
        }
        return crearGraficoBarras("Usuarios con mayor consumo (kWh/mes)", etiquetas, valores);
    }

    private JPanel crearGraficoBarrasTopDispositivos(List<Usuario> usuarios) {
        Map<String, Double> consumoDispositivos = new HashMap<>();
        for (Usuario usuario : usuarios) {
            for (Inmueble inmueble : usuario.getInmuebles()) {
                for (Dispositivo dispositivo : inmueble.getDispositivos()) {
                    String nombre = dispositivo.getNombre();
                    consumoDispositivos.put(nombre,
                            consumoDispositivos.getOrDefault(nombre, 0.0) + dispositivo.calcularConsumoMensual());
                }
            }
        }
        if (consumoDispositivos.isEmpty()) {
            return crearPanelMensajeGrafico("No hay dispositivos con consumo para analizar.");
        }

        List<Map.Entry<String, Double>> ranking = new ArrayList<>(consumoDispositivos.entrySet());
        ranking.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        int top = Math.min(6, ranking.size());
        String[] etiquetas = new String[top];
        double[] valores = new double[top];
        for (int i = 0; i < top; i++) {
            etiquetas[i] = ranking.get(i).getKey();
            valores[i] = ranking.get(i).getValue();
        }
        return crearGraficoBarras("Top dispositivos por consumo (kWh/mes)", etiquetas, valores);
    }

    private JPanel crearGraficoPastelNivelesConsumo(List<Usuario> usuarios) {
        Map<String, Integer> conteo = new HashMap<>();
        for (Usuario usuario : usuarios) {
            String nivel = sistema.getAnalizador().clasificarConsumo(usuario);
            conteo.put(nivel, conteo.getOrDefault(nivel, 0) + 1);
        }
        String[] etiquetas = {"BAJO", "NORMAL", "ALTO", "CRITICO"};
        double[] valores = {
                conteo.getOrDefault("BAJO", 0),
                conteo.getOrDefault("NORMAL", 0),
                conteo.getOrDefault("ALTO", 0),
                conteo.getOrDefault("CRITICO", 0)
        };
        return crearGraficoPastel("Distribución por nivel de consumo", etiquetas, valores);
    }

    private JPanel crearGraficoPastelSectores(List<Usuario> usuarios) {
        Map<Sector, Integer> conteo = new HashMap<>();
        for (Usuario usuario : usuarios) {
            conteo.put(usuario.getSector(), conteo.getOrDefault(usuario.getSector(), 0) + 1);
        }

        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        for (Sector sector : Sector.values()) {
            int cantidad = conteo.getOrDefault(sector, 0);
            if (cantidad > 0) {
                etiquetas.add(sector.name());
                valores.add((double) cantidad);
            }
        }
        if (valores.isEmpty()) {
            return crearPanelMensajeGrafico("No hay sectores para representar.");
        }
        return crearGraficoPastel(
                "Distribución de usuarios por sector",
                etiquetas.toArray(new String[0]),
                valores.stream().mapToDouble(Double::doubleValue).toArray()
        );
    }

    private JPanel crearPanelMensajeGrafico(String mensaje) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        JLabel lbl = new JLabel(mensaje, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXTO_SUAVE);
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearGraficoBarras(String titulo, String[] etiquetas, double[] valores) {
        JPanel grafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 14, 14);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

                g2.setColor(COLOR_TEXTO);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(titulo, 12, 22);

                if (valores == null || valores.length == 0) return;
                double max = 0;
                for (double v : valores) if (v > max) max = v;
                if (max <= 0) return;

                int left = 40;
                int right = 16;
                int top = 34;
                int bottom = 36;
                int chartW = w - left - right;
                int chartH = h - top - bottom;
                if (chartW <= 0 || chartH <= 0) return;

                g2.setColor(new Color(230, 236, 247));
                g2.fillRect(left, top, chartW, chartH);

                int n = valores.length;
                int barSpace = Math.max(4, chartW / Math.max(1, n * 2));
                int barWidth = Math.max(10, (chartW - (n + 1) * barSpace) / Math.max(1, n));
                Color[] paleta = {
                        new Color(21, 101, 192), new Color(56, 142, 60), new Color(230, 111, 0),
                        new Color(123, 31, 162), new Color(0, 131, 143), new Color(198, 40, 40)
                };

                for (int i = 0; i < n; i++) {
                    int x = left + barSpace + i * (barWidth + barSpace);
                    int barH = (int) ((valores[i] / max) * (chartH - 10));
                    int y = top + chartH - barH;
                    g2.setColor(paleta[i % paleta.length]);
                    g2.fillRoundRect(x, y, barWidth, barH, 8, 8);

                    g2.setColor(COLOR_TEXTO);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    String valor = String.format("%.1f", valores[i]);
                    int valorW = g2.getFontMetrics().stringWidth(valor);
                    g2.drawString(valor, x + (barWidth - valorW) / 2, Math.max(top + 12, y - 4));

                    String etiqueta = etiquetas != null && i < etiquetas.length ? etiquetas[i] : ("Item " + (i + 1));
                    if (etiqueta.length() > 14) etiqueta = etiqueta.substring(0, 14) + "...";
                    int etW = g2.getFontMetrics().stringWidth(etiqueta);
                    g2.drawString(etiqueta, x + (barWidth - etW) / 2, h - 14);
                }
            }
        };
        grafico.setPreferredSize(new Dimension(320, 220));
        grafico.setMinimumSize(new Dimension(260, 180));
        return grafico;
    }

    private JPanel crearGraficoPastel(String titulo, String[] etiquetas, double[] valores) {
        JPanel grafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w, h, 14, 14);
                g2.setColor(COLOR_BORDE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

                g2.setColor(COLOR_TEXTO);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(titulo, 12, 22);

                if (valores == null || valores.length == 0) return;
                double total = 0;
                for (double v : valores) total += Math.max(0, v);
                if (total <= 0) return;

                int diameter = Math.min(h - 50, w / 2);
                diameter = Math.max(80, diameter);
                int x = 16;
                int y = 34;
                Color[] paleta = {
                        new Color(21, 101, 192), new Color(56, 142, 60), new Color(230, 111, 0),
                        new Color(123, 31, 162), new Color(0, 131, 143), new Color(198, 40, 40),
                        new Color(2, 119, 189), new Color(93, 64, 55)
                };

                int start = 0;
                for (int i = 0; i < valores.length; i++) {
                    int angle = (int) Math.round((Math.max(0, valores[i]) * 360.0) / total);
                    g2.setColor(paleta[i % paleta.length]);
                    g2.fillArc(x, y, diameter, diameter, start, angle);
                    start += angle;
                }

                int lx = x + diameter + 18;
                int ly = y + 8;
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                for (int i = 0; i < valores.length; i++) {
                    double porcentaje = (Math.max(0, valores[i]) * 100.0) / total;
                    if (porcentaje <= 0) continue;
                    g2.setColor(paleta[i % paleta.length]);
                    g2.fillRoundRect(lx, ly - 9, 12, 12, 3, 3);
                    g2.setColor(COLOR_TEXTO);
                    String etiqueta = etiquetas != null && i < etiquetas.length ? etiquetas[i] : ("Item " + (i + 1));
                    if (etiqueta.length() > 20) etiqueta = etiqueta.substring(0, 20) + "...";
                    g2.drawString(etiqueta + " (" + String.format("%.1f", porcentaje) + "%)", lx + 18, ly + 1);
                    ly += 18;
                    if (ly > h - 12) break;
                }
            }
        };
        grafico.setPreferredSize(new Dimension(320, 220));
        grafico.setMinimumSize(new Dimension(260, 180));
        return grafico;
    }

    private String formatearReporteAdministrativoHtml(String textoPlano) {
        String t = textoPlano == null ? "" : textoPlano;
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='")
          .append("font-family:\"Segoe UI\",Arial,sans-serif;")
          .append("font-size:14px; color:#1c1f3a;")
          .append("line-height:1.65; margin:12px 18px;'>");

        for (String l : t.split("\\R")) {
            String raw = l == null ? "" : l.trim();
            if (raw.isEmpty()) {
                sb.append("<div style='margin:4px 0;'>&nbsp;</div>");
                continue;
            }

            String line = escaparHtml(raw);
            boolean separador = raw.matches("[-=]{8,}");
            boolean titulo = raw.matches("^[A-ZÁÉÍÓÚÑ0-9\\s]+$") && raw.length() > 8;

            if (separador) {
                sb.append("<div style='border-bottom:1px solid #d8deed; margin:6px 0;'></div>");
                continue;
            }

            if (titulo) {
                sb.append("<div style='font-weight:700; color:#0d47a1; margin:6px 0;'>")
                  .append(line)
                  .append("</div>");
                continue;
            }

            if (raw.startsWith("- ")) {
                sb.append("<div style='margin-left:8px;'>• ")
                  .append(escaparHtml(raw.substring(2)))
                  .append("</div>");
                continue;
            }

            int sep = raw.indexOf(": ");
            if (sep > 0) {
                String etiqueta = escaparHtml(raw.substring(0, sep + 1));
                String valor = escaparHtml(raw.substring(sep + 2));
                sb.append("<div><b>").append(etiqueta).append("</b> ").append(valor).append("</div>");
            } else {
                sb.append("<div>").append(line).append("</div>");
            }
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    private String formatearInformeHtml(String textoPlano, boolean resaltarCritico) {
        String t = textoPlano == null ? "" : textoPlano;

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='"
                + "font-family: Georgia, \"Times New Roman\", serif; "
                + "font-size: 14px; color: #1c1f3a; "
                + "line-height: 1.85; margin: 12px 18px;'>");

        String[] lineas = t.split("\\R");
        for (String l : lineas) {
            String raw  = l == null ? "" : l.trim();
            String line = escaparHtml(raw);

            if (line.isEmpty()) {
                sb.append("<div style='margin:4px 0;'>&nbsp;</div>");
                continue;
            }

            String x = raw.toLowerCase();

            boolean esTitulo  = raw.startsWith("====") || raw.endsWith(":")
                    || x.contains("resumen") || x.contains("analisis")
                    || x.contains("análisis") || x.contains("factura")
                    || x.contains("recomend");
            boolean esCritico = x.contains("critico") || x.contains("crítico");
            boolean esAlto    = x.contains("alto");
            boolean esMedio   = x.contains("medio") || x.contains("normal")
                             || x.contains("moderado") || x.contains("moderada");
            boolean esBajo    = x.contains("bajo");

            if (esTitulo) {
                sb.append("<div style='"
                        + "font-family:\"Segoe UI\",Arial,sans-serif; font-weight:bold; "
                        + "font-size:14px; color:#0d47a1; "
                        + "border-bottom:1px solid #c5cae9; "
                        + "margin-top:14px; margin-bottom:4px; padding-bottom:4px;'>")
                  .append(line).append("</div>");
            } else if (resaltarCritico && (esCritico || esAlto)) {
                sb.append("<div style='color:#b71c1c; font-weight:600;'>⚠&nbsp;").append(line).append("</div>");
            } else if (resaltarCritico && esMedio) {
                sb.append("<div style='color:#e65100; font-weight:600;'>").append(line).append("</div>");
            } else if (resaltarCritico && esBajo) {
                sb.append("<div style='color:#2e7d32; font-weight:600;'>").append(line).append("</div>");
            } else {
                sb.append("<div>").append(line).append("</div>");
            }
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    /**
     * Formatea las recomendaciones con un banner de color según el nivel de consumo:
     * BAJO → verde, NORMAL → azul, ALTO → ámbar, CRITICO → rojo.
     */
    private String formatearRecomendacionesHtml(String textoPlano, String nivel) {
        String colorBorde, colorTexto, colorFondo, icono, etiqueta;
        switch (nivel == null ? "" : nivel.toUpperCase().trim()) {
            case "BAJO":
                colorBorde = "#2e7d32"; colorTexto = "#1b5e20";
                colorFondo = "#f1f8e9"; icono = "✅"; etiqueta = "Consumo Óptimo";
                break;
            case "NORMAL":
                colorBorde = "#1565c0"; colorTexto = "#0d47a1";
                colorFondo = "#e8f0fe"; icono = "ℹ"; etiqueta = "Consumo Normal";
                break;
            case "ALTO":
                colorBorde = "#e65100"; colorTexto = "#bf360c";
                colorFondo = "#fff3e0"; icono = "⚠"; etiqueta = "Consumo Elevado";
                break;
            case "CRITICO":
                colorBorde = "#c62828"; colorTexto = "#b71c1c";
                colorFondo = "#ffebee"; icono = "🚨"; etiqueta = "Consumo Crítico";
                break;
            default:
                colorBorde = "#546e7a"; colorTexto = "#37474f";
                colorFondo = "#f5f5f5"; icono = "📋"; etiqueta = "Recomendaciones";
        }

        String t = textoPlano == null ? "" : textoPlano;
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='"
                + "font-family: Georgia, \"Times New Roman\", serif; "
                + "font-size: 14px; color: #1c1f3a; "
                + "line-height: 1.85; margin: 12px 18px;'>");

        // ── Banner de estado ─────────────────────────────────────────────────
        sb.append("<div style='"
                + "background:").append(colorFondo).append("; "
                + "border-left:5px solid ").append(colorBorde).append("; "
                + "padding:10px 14px; margin-bottom:18px; "
                + "font-family:\"Segoe UI\",Arial,sans-serif; "
                + "font-size:13px; color:").append(colorTexto).append("; font-weight:bold;'>")
          .append(icono).append("&nbsp; Estado de consumo: ").append(etiqueta)
          .append("</div>");

        // ── Contenido con colores según gravedad ─────────────────────────────
        for (String l : t.split("\\R")) {
            String raw  = l == null ? "" : l.trim();
            String line = escaparHtml(raw);
            if (line.isEmpty()) { sb.append("<div style='margin:3px 0;'>&nbsp;</div>"); continue; }

            String x = raw.toLowerCase();
            boolean esTitulo  = raw.startsWith("====") || raw.endsWith(":")
                    || x.contains("recomend") || x.contains("analisis");
            boolean esCritico = x.contains("critico") || x.contains("crítico");
            boolean esAlto    = x.contains("alto");
            boolean esMedio   = x.contains("medio") || x.contains("normal") || x.contains("moderado");
            boolean esBajo    = x.contains("bajo")  || x.contains("optimo") || x.contains("óptimo");

            if (esTitulo) {
                sb.append("<div style='font-family:\"Segoe UI\",Arial,sans-serif; font-weight:bold; "
                        + "font-size:14px; color:#0d47a1; border-bottom:1px solid #c5cae9; "
                        + "margin-top:12px; padding-bottom:4px;'>").append(line).append("</div>");
            } else if (esCritico || esAlto) {
                sb.append("<div style='color:#b71c1c; font-weight:600;'>⚠&nbsp;").append(line).append("</div>");
            } else if (esMedio) {
                sb.append("<div style='color:#e65100;'>").append(line).append("</div>");
            } else if (esBajo) {
                sb.append("<div style='color:#2e7d32;'>✓&nbsp;").append(line).append("</div>");
            } else {
                sb.append("<div>").append(line).append("</div>");
            }
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    private double calcularConsumoDiarioKwh(Usuario usuario) {
        if (usuario == null || inmuebleActual == null) return 0.0;
        double total = 0.0;
        for (Dispositivo d : inmuebleActual.getDispositivos()) {
            total += (d.getPotencia() * d.getCantidad() * d.getHorasUsoDiarias()) / 1000.0;
        }
        return total;
    }

    private void cargarTablaDispositivos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        if (usuarioActual == null || inmuebleActual == null) return;
        for (Dispositivo d : inmuebleActual.getDispositivos()) {
            double consumo = (d.getPotencia() * d.getCantidad() * d.getHorasUsoDiarias()) / 1000.0;
            modelo.addRow(new Object[]{
                d.getId(), d.getNombre(),
                String.format("%.0f", d.getPotencia()),
                d.getCantidad(),
                String.format("%.2f", d.getHorasUsoDiarias()),
                String.format("%.2f", consumo)
            });
        }
    }

    private void actualizarInicio() {
        if (pestanas == null || usuarioActual == null || usuarioActual.getRol() == Rol.ADMINISTRADOR) {
            return;
        }

        if (usuarioActual.isActivo()) {
            if (pestanas.getTabCount() >= 7) {
                pestanas.setComponentAt(0, crearPestanaInicio());
                pestanas.setComponentAt(1, crearPestanaDispositivos());
                pestanas.setComponentAt(2, crearPestanaResumen());
                pestanas.setComponentAt(3, crearPestanaFactura());
                pestanas.setComponentAt(4, crearPestanaRecomendaciones());
                pestanas.setComponentAt(5, crearPestanaConsumoGlobal());
                pestanas.setComponentAt(6, crearPestanaRetos());
            }
        } else {
            if (pestanas.getTabCount() >= 3) {
                pestanas.setComponentAt(0, crearPestanaResumen());
                pestanas.setComponentAt(1, crearPestanaFactura());
                pestanas.setComponentAt(2, crearPestanaConsumoGlobal());
            }
        }

        pestanas.revalidate();
        pestanas.repaint();
    }
}
