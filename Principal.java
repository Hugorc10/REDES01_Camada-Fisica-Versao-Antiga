/**
 * Autor: Hugo Teixeira Mafra
 * Matricula: 201611540
 * Inicio: 09/02/2018
 * Ultima alteracao: 18/02/2018
 * Nome: Codificador de Sinal Digital
 * Funcao: simular o funcionamento do enlace fisico - independente do meio de transmissao -
 * atraves da implementacao de tres tipos de codificacoes estudadas no livro de Redes de Computadores, do Tanenbaum
 */

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Classe principal
public class Principal extends JFrame {
  private final Box choiceBox;
  private final ButtonGroup optionButtonGroup;
  // Vetor que ira armazenar o fluxo de bits contendo 32 bits cada posicao do mesmo
  private int[] fluxoBrutoDeBits;
  // Instancias de variaveis da biblioteca swing
  private JPanel painelPrincipal = new JPanel();
  private JTextField textoEnviar = new JTextField(35);
  private JTextArea bitsReceptor = new JTextArea(1, 35);
  private JTextArea mensagemReceptor = new JTextArea(1, 35);
  private JButton iniciarButton = new JButton("Iniciar");
  private JButton limparButton = new JButton("Limpar");
  private JRadioButton binarioRadioButton = new JRadioButton("Binario");
  private JRadioButton manchesterRadioButton = new JRadioButton("Manchester");
  private JRadioButton manchesterDiferencialRadioButton = new JRadioButton("Manchester Diferencial");
  private Box drawBox;
  /* Instancia da classe RepresentacaoGraficaMeioDeTransmissao,
  ela ira realizar o desenho grafico das linhas de codificacao */
  private RepresentacaoGraficaMeioDeTransmissao camadaFisica = new RepresentacaoGraficaMeioDeTransmissao();

  /**
   * Metodo: Principal
   * Funcao: construtor da classe Principal. Organiza e Instancia a parte grafica do projeto
   */
  public Principal() {
    painelPrincipal.setLayout(new GridBagLayout()); // Seta o layout do JPanel como GridBagLayout

    addItem(painelPrincipal, new JLabel("Letras a enviar:"), 0, 0, 1, 1, GridBagConstraints.WEST);
    addItem(painelPrincipal, new JLabel("Bits enviados:"), 0, 2, 1, 1, GridBagConstraints.WEST);
    addItem(painelPrincipal, new JLabel("Mensagem recebida:"), 0, 3, 1, 1, GridBagConstraints.WEST);

    addItem(painelPrincipal, textoEnviar, 1, 0, 1, 1, GridBagConstraints.WEST);

    Border border1 = BorderFactory.createLineBorder(Color.black);
    bitsReceptor.setBorder(BorderFactory.createCompoundBorder(border1, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    bitsReceptor.setEditable(false);
    JScrollPane scroller1 = new JScrollPane(bitsReceptor); // Adiciona um "scroller" a area de texto "bitsReceptor"
    scroller1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); // Ira aparecer caso necessario
    addItem(painelPrincipal, scroller1, 1, 2, 1, 1, GridBagConstraints.WEST);


    Box buttonBox = Box.createHorizontalBox();
    buttonBox.add(iniciarButton);
    buttonBox.add(Box.createHorizontalStrut(20));
    buttonBox.add(limparButton);
    addItem(painelPrincipal, buttonBox, 1, 1, 1, 1, GridBagConstraints.WEST);
    iniciarButton.addActionListener(new ActionListener() { // Acao do botao "Iniciar"
      @Override
      public void actionPerformed(ActionEvent e) {
        aplicacaoTransmissora(e);
      }
    });
    limparButton.addActionListener(new ActionListener() { // Acao do botao "Limpar"
      @Override
      public void actionPerformed(ActionEvent e) {
        limpaTela(e);
      }
    });

    Border border2 = BorderFactory.createLineBorder(Color.black);
    mensagemReceptor.setBorder(BorderFactory.createCompoundBorder(border2, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    mensagemReceptor.setEditable(false);
    addItem(painelPrincipal, mensagemReceptor, 1, 3, 1, 1, GridBagConstraints.WEST);

    optionButtonGroup = new ButtonGroup();
    optionButtonGroup.add(binarioRadioButton);
    optionButtonGroup.add(manchesterRadioButton);
    optionButtonGroup.add(manchesterDiferencialRadioButton);
    choiceBox = Box.createVerticalBox();
    choiceBox.add(binarioRadioButton);
    choiceBox.add(manchesterRadioButton);
    choiceBox.add(manchesterDiferencialRadioButton);
    choiceBox.setBorder(BorderFactory.createTitledBorder("Tipo de Codificacao"));
    binarioRadioButton.setSelected(true); // A opcao "Binario" ira ser selecionada automaticamente
    binarioRadioButton.setBackground(SystemColor.control);
    manchesterRadioButton.setBackground(SystemColor.control);
    manchesterDiferencialRadioButton.setBackground(SystemColor.control);
    addItem(painelPrincipal, choiceBox, 5, 0, 0, 0, GridBagConstraints.NORTH);

    JScrollPane scroller2 = new JScrollPane(camadaFisica);
    scroller2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroller2.setPreferredSize(new Dimension(600, 200));
    drawBox = Box.createHorizontalBox();
    drawBox.add(scroller2);
    drawBox.setBorder(BorderFactory.createTitledBorder("Desenho da Codificacao"));
    addItem(painelPrincipal, drawBox, 0, 5, 0, 0, GridBagConstraints.WEST);

    this.add(painelPrincipal);
    this.setTitle("Codificador de Sinal Digital"); // Define o titulo
    this.setLocationByPlatform(true); // Define se a janela deve aparecer na localizacao padrao do Sistema Operacional
    this.pack(); // A janela sera ajustada de acordo com o numero de subcomponentes
    this.setResizable(false); // Nao permite expandir a janela
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Finaliza o programa ao fechar a janela
  }

  // Metodo main
  public static void main(String[] args) {
    // O Swing nao eh thread-safe, precisa ser executado dentro de um Runnable
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Principal principal = new Principal(); // Instancia da classe Principal
        principal.setVisible(true); // Seta o JFrame como visivel
      }
    });
  } // Fim do metodo main

  /**
   * Metodo: meioDeTransmissaoCodificacaoBinaria
   * Funcao: Codifica um vetor de bytes contendo os codigos ASCII de caracteres alfabeticos
   * para a codificacao Binaria e insere em um array de inteiros em forma de bits
   *
   * @param quadro = array de bytes contendo codigos ASCII de cada caracter alfabetico
   * @return int[]
   */
  public static int[] camadaFisicaTransmissoraCodificacaoBinaria(byte[] quadro) {
    System.out.print("\nCamada Fisica Transmissora Codificacao Binaria\n");

    // Variavel que ira receber o comprimento (length) do array "quadro" dividido por quatro
    int n = quadro.length / 4;

    // Verifica o comprimento do quadro divido por quatro tiver resto diferente de 0
    if (quadro.length % 4 != 0)
      n++; // Soma +1 a variavel n

    int[] bits = new int[n]; // Array que ira conter os inteiros com os bits armazenados no array "quadro"

    int aux = 0; // Variavel auxiliar

    // Realiza loop ate o i ser menor que o comprimento (length) do vetor bits
    for (int i = 0; i < bits.length; i++) {
      bits[i] = quadro[aux]; // bits[0] = quadro[0]
      aux++; // aux = 1

      while (aux < quadro.length) {
        bits[i] = bits[i] << 8; // Desloca 8 bits a esquerda
        bits[i] = bits[i] | quadro[aux]; // Concatena os bits na posicao "i" com os bits do quadro na posicao "aux"
        aux++; // aux = 2
      } // Fim do while
    } // Fim do for

    return bits;
  } // Fim do metodo camadaFisicaTransmissoraCodificacaoBinaria

  /**
   * Metodo: aplicacaoTransmissora
   * Funcao: armazenar o texto digitado em uma string e envia-lo para o metodo "camadaAplicacaoTransmissora()"
   *
   * @param e realiza o evento
   * @return void
   */
  private void aplicacaoTransmissora(ActionEvent e) {
    String mensagem = textoEnviar.getText(); // Armazena a texto digitado na String mensagem
    camadaAplicacaoTransmissora(e, mensagem); // Chama a proxima camada
  } // Fim do metodo aplicacaoTransmissora

  /**
   * Metodo: camadaAplicacaoTransmissora
   * Funcao: guarda os codigos ASCII em um array de bytes e o envia para o metodo "camadaFisicaTransmissora()"
   *
   * @param e
   * @param mensagem
   * @return void
   */
  private void camadaAplicacaoTransmissora(ActionEvent e, String mensagem) {
    byte[] quadro = mensagem.getBytes(); // Insere um codigo ASCII em cada posicao do vetor

    if (mensagem.matches("[a-zA-Z]+") || mensagem.matches("[0-9]+")) {
      camadaFisicaTransmissora(e, quadro);
    } else if (textoEnviar.getText().isEmpty()) {
      JOptionPane.showMessageDialog(null, "A caixa de texto esta vazia", "Error", JOptionPane.ERROR_MESSAGE);
    } else {
      camadaFisicaTransmissora(e, quadro);
    }
  } // Fim do metodo camadaAplicacaoTransmissora

  /**
   * Metodo: limpaTela
   * Funcao: limpa o painel e os campos de texto exibidos na aplicacao
   *
   * @param e realiza a acao
   * @return void
   */
  private void limpaTela(ActionEvent e) {
    camadaFisica.clear = true; // Faz limpar a tela
    bitsReceptor.setText("");
    mensagemReceptor.setText("");
    repaint(); // Repinta a tela
  } // Fim do metodo limpaTela

  private void camadaFisicaTransmissora(ActionEvent e, byte[] quadro) {
    camadaFisica.clear = false; // Impede de limpar a tela

    if (binarioRadioButton.isSelected()) {
      try {
        limpaTela(e);
        camadaFisica.clear = false;

        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoBinaria(quadro);

        camadaFisica.bits = new String[fluxoBrutoDeBits.length];
        for (int i = 0; i < fluxoBrutoDeBits.length; i++)
          camadaFisica.bits[i] = Integer.toBinaryString(fluxoBrutoDeBits[i]);

        camadaFisica.setEncodingTechnique(camadaFisica.BINARIO);
        camadaFisica.repaint();

        for (String s : camadaFisica.bits)
          bitsReceptor.append(s);
      } catch (NumberFormatException e1) {
        JOptionPane.showMessageDialog(this, "Entrada Invalida", "Error", JOptionPane.ERROR_MESSAGE);
      } catch (Exception e1) {
        JOptionPane.showMessageDialog(this,
                "Excecao Desconhecida Ocorreu",
                "Exception",
                JOptionPane.ERROR_MESSAGE);
      }
    } else if (manchesterRadioButton.isSelected()) {
      try {
        limpaTela(e);
        camadaFisica.clear = false;

        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchester(quadro);

        camadaFisica.manchesterBits = new String[fluxoBrutoDeBits.length];
        for (int i = 0; i < fluxoBrutoDeBits.length; i++) {
          camadaFisica.manchesterBits[i] = Integer.toBinaryString(fluxoBrutoDeBits[i]);
        }

        camadaFisica.setEncodingTechnique(camadaFisica.MANCHESTER);
        camadaFisica.repaint();

        for (String s : camadaFisica.manchesterBits)
          bitsReceptor.append(s);
      } catch (NumberFormatException e1) {
        JOptionPane.showMessageDialog(this, "Entrada Invalida", "Error", JOptionPane.ERROR_MESSAGE);
      } catch (Exception e1) {
        JOptionPane.showMessageDialog(this,
                "Excecao Desconhecida Ocorreu",
                "Exception",
                JOptionPane.ERROR_MESSAGE);
      }
    } else if (manchesterDiferencialRadioButton.isSelected()) {
      try {
        limpaTela(e);
        camadaFisica.clear = false;

        fluxoBrutoDeBits = camadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);

        camadaFisica.manchesterDiferencialBits = new String[fluxoBrutoDeBits.length];
        for (int i = 0; i < fluxoBrutoDeBits.length; i++) {
          camadaFisica.manchesterDiferencialBits[i] = Integer.toBinaryString(fluxoBrutoDeBits[i]);
        }

        camadaFisica.setEncodingTechnique(camadaFisica.MANCHESTER_DIFERENCIAL);
        camadaFisica.repaint();

        for (String s : camadaFisica.manchesterDiferencialBits)
          bitsReceptor.append(s);
      } catch (NumberFormatException e1) {
        JOptionPane.showMessageDialog(this, "Entrada Invalida", "Error", JOptionPane.ERROR_MESSAGE);
      } catch (Exception e1) {
        JOptionPane.showMessageDialog(this,
                "Excecao Desconhecida Ocorreu",
                "Exception",
                JOptionPane.ERROR_MESSAGE);
      }
    }
    camadaFisica.repaint();
    meioDeComunicacao(fluxoBrutoDeBits);
  }

  /**
   * Metodo: camadaFisicaTransmissoraCodificacaoManchester
   * Funcao: codifica um vetor de bytes contendo os codigos ASCII de caracteres alfabeticos
   * para a codificacao Manchester e insere em um array de inteiros em forma de bits
   *
   * @param quadro
   * @return int[]
   */
  private int[] camadaFisicaTransmissoraCodificacaoManchester(byte[] quadro) {
    System.out.print("\nCamada Fisica Transmissora Codificacao Manchester\n");

    // Variavel que ira receber o comprimento (length) do array "quadro" dividido por dois
    int n = quadro.length / 2;

    // Verifica o comprimento do quadro divido por quatro tiver resto diferente de 0
    if (quadro.length % 2 != 0) {
      n++;
    }

    // Array que ira conter os inteiros com os bits armazenados no array "quadro"
    int[] bits = new int[n];

    int aux = 0; // Variavel auxiliar
    for (int x = 0; x < bits.length; x++) {
      for (int y = 0; y < 2; y++) {
        if (aux < quadro.length) {
          int temp = quadro[aux];
          for (int i = 7; i >= 0; i--) {
            bits[x] = bits[x] << 1;
            int mask = 1 << i; // Desloca 7 bits a esquerda ate 0
            if (Integer.toBinaryString(temp & mask).charAt(0) == '0') {
              bits[x] = bits[x] | 1; // Adiciona o bit 1
            } else if (Integer.toBinaryString(temp & mask).charAt(0) == '1') {
              bits[x] = bits[x] | 0; // Adiciona o bit 0
            }
            int t = Integer.parseInt(String.valueOf(Integer.toBinaryString(temp & mask).charAt(0)));
            bits[x] = bits[x] << 1;
            bits[x] = bits[x] | t;
          }
          aux++;
        }
      }
    }

    return bits;
  } // Fim do metodo camadaFisicaTransmissoraCodificacaoManchester

  /**
   * Metodo: camadaFisicaTransmissoraCodificacaoManchesterDiferencial
   * Funcao: Codifica um vetor de bytes contendo os codigos ASCII de caracteres alfabeticos
   * para a codificacao Manchester Diferencial e insere em um array de inteiros em forma de bits
   *
   * @param quadro
   * @return int[]
   */
  private int[] camadaFisicaTransmissoraCodificacaoManchesterDiferencial(byte[] quadro) {
    System.out.println("\nCamada Fisica Transmissora Codificacao Manchester Diferencial\n");

    // Variavel que ira receber o comprimento (length) do array "quadro" dividido por dois
    int n = quadro.length / 2;

    // Verifica o comprimento do quadro divido por quatro tiver resto diferente de 0
    if (quadro.length % 2 != 0) {
      n++;
    }

    // Array que ira conter os inteiros com os bits armazenados no array "quadro"
    int[] bits = new int[n];

    int aux = 0; // Variavel auxiliar
    for (int x = 0; x < bits.length; x++) {
      for (int y = 0; y < 2; y++) {
        if (aux < quadro.length) {
          int temp = quadro[aux];

          for (int i = 7; i >= 0; i--) {
            bits[x] = bits[x] << 1;
            int mask = 1 << i;
            if (Integer.toBinaryString(temp & mask).charAt(0) == '0') {
              bits[x] = bits[x] | 1;
            } else if (Integer.toBinaryString(temp & mask).charAt(0) == '1') {
              bits[x] = bits[x] | 0;
            }
            int t = Integer.parseInt(String.valueOf(Integer.toBinaryString(temp & mask).charAt(0)));
            bits[x] = bits[x] << 1;
            bits[x] = bits[x] | t;
          }
          aux++;
        }
      }
    }

    return bits;
  } // Fim do metodo camadaFisicaTransmissoraCodificacaoManchesterDiferencial

  /**
   * Metodo: meioDeComunicacao
   * Funcao: simular a transmissao de comunicacao da informacao no meio de comunicacao
   * passando de uma variavel para outra
   *
   * @param fluxoBrutoDeBits eh o fluxo de binarios codificados
   * @return void
   */
  public void meioDeComunicacao(int[] fluxoBrutoDeBits) {
    System.out.print("\nMeio de Comunicacao\n");

    int[] fluxoBrutoDeBitsPontoA = new int[fluxoBrutoDeBits.length];

    for (int i = 0; i < fluxoBrutoDeBits.length; i++) {
      fluxoBrutoDeBitsPontoA[i] = fluxoBrutoDeBits[i];
    }

    int[] fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBits.length];

    for (int i = 0; i < fluxoBrutoDeBitsPontoA.length; i++) {
      // fluxoBrutoDeBitsPontoB[i] = (fluxoBrutoDeBitsPontoA[i] and 0000 0000) or (fluxoBrutoDeBitsPontoA[i] and 1111 1111)
      fluxoBrutoDeBitsPontoB[i] = (fluxoBrutoDeBitsPontoA[i] & ~0xff) | (fluxoBrutoDeBitsPontoA[i] & 0xff);
    }

    camadaFisicaReceptora(fluxoBrutoDeBitsPontoB);
  } // Fim do metodo meioDeComunicacao

  /**
   * Metodo: camadaFisicaReceptora
   * Funcao: recebe os bits do vetor de inteiros e decodifica de acordo
   * com o tipo de codificacao
   *
   * @param quadro
   * @return void
   */
  private void camadaFisicaReceptora(int[] quadro) {
    System.out.print("\nCamada Fisica Receptora\n");
    final int BINARY = 1, MANCHESTER = 2, MANCHESTER_DIFERENCIAL = 3;
    int[] fluxoBrutoDeBits = new int[0];

    switch (camadaFisica.getEncodingTechnique()) {
      case BINARY:
        fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoBinaria(quadro);
        break;
      case MANCHESTER:
        fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoManchester(quadro);
        break;
      case MANCHESTER_DIFERENCIAL:
        fluxoBrutoDeBits = camadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro);
        break;
      default:
        break;
    } // Fim do switch/case
    // chama proxima camada
    camadaDeAplicacaoReceptora(fluxoBrutoDeBits);
  } // Fim do metodo camadaFisicaReceptora

  /**
   * Metodo: camadaFisicaReceptoraDecodificacaoBinaria
   * Funcao:
   *
   * @param quadro
   * @return int[]
   */
  private int[] camadaFisicaReceptoraDecodificacaoBinaria(int[] quadro) {
    System.out.print("\nCamada Fisica Receptora Decodificacao Binaria\n");
    int[] fluxoBrutoDeBits = new int[quadro.length * 4];

    for (int x = 0; x < fluxoBrutoDeBits.length; x++) {
      fluxoBrutoDeBits[x] = -1;
    }

    int i = 31;
    if (quadro[quadro.length - 1] <= 255) {
      i = 7;
    } else if (quadro[quadro.length - 1] <= 65535) {
      i = 15;
    } else if (quadro[quadro.length - 1] <= 16777215) {
      i = 23;
    }

    int aux2 = 0;
    for (int x = 0; x < quadro.length; x++) {
      int y = 31;
      if (x == quadro.length - 1) {
        y = i;
      }
      String imp = "";
      int aux1 = 1;

      while (y >= 0) {
        int mask = 1 << y;
        imp += "" + Integer.toBinaryString(quadro[x] & mask).charAt(0);

        if (aux1 != 0 && aux1 % 8 == 0) {
          fluxoBrutoDeBits[aux2] = Integer.parseInt(imp, 2);
          aux2++;
          imp = "";
        }
        aux1++;
        y--;
      }
    }
    return fluxoBrutoDeBits;
  } // Fim do metodo camadaFisicaReceptoraDecodificacaoBinaria

  /**
   * Metodo: camadaFisicaReceptoraDecoficacaoManchester
   * Funcao:
   *
   * @param quadro
   * @return int[]
   */
  public int[] camadaFisicaReceptoraDecodificacaoManchester(int[] quadro) {
    System.out.print("\nCamada Fisica Receptora Decodificacao Manchester\n");
    int[] fluxoBrutoDeBits = new int[quadro.length * 2];

    for (int x = 0; x < fluxoBrutoDeBits.length; x++)
      fluxoBrutoDeBits[x] = -1;

    int i = 31;

    String n = "" + quadro[quadro.length - 1];
    if (n.length() <= 6) {
      i = 15;
    }

    int aux2 = 0;
    for (int x = 0; x < quadro.length; x++) {
      int y = 31;
      if (x == quadro.length - 1) {
        y = i;
      }
      String imp = "";
      int aux1 = 1;

      while (y >= 0) {
        int mask = 1 << y;

        if ((aux1 - 1 != 0) && (aux1 - 1) % 2 != 0) {
          imp += "" + Integer.toBinaryString(quadro[x] & mask).charAt(0);
        }

        if (aux1 != 0 && aux1 % 16 == 0) {
          fluxoBrutoDeBits[aux2] = Integer.parseInt(imp, 2);
          aux2++;
          imp = "";
        }
        aux1++;
        y--;
      }
    }
    return fluxoBrutoDeBits;
  } // Fim do metodo camadaFisicaReceptoraDecodificacaoManchester

  /**
   * Metodo: camadaFisicaReceptoraDecodificacaoManchesterDiferencial
   * Funcao:
   *
   * @param quadro
   * @return int[]
   */
  private int[] camadaFisicaReceptoraDecodificacaoManchesterDiferencial(int[] quadro) {
    System.out.print("\nCamada Fisica Receptora Decodificacao Manchester Diferencial\n");
    int[] fluxoBrutoDeBits = new int[quadro.length * 2];

    for (int x = 0; x < fluxoBrutoDeBits.length; x++)
      fluxoBrutoDeBits[x] = -1;

    int i = 31;

    String n = "" + quadro[quadro.length - 1];
    if (n.length() <= 6) {
      i = 15;
    }

    int aux2 = 0;
    for (int x = 0; x < quadro.length; x++) {
      int y = 31;
      if (x == quadro.length - 1) {
        y = i;
      }
      String imp = "";
      int aux1 = 1;

      while (y >= 0) {
        int mask = 1 << y;

        if ((aux1 - 1 != 0) && (aux1 - 1) % 2 != 0) {
          imp += "" + Integer.toBinaryString(quadro[x] & mask).charAt(0);
        }

        if (aux1 != 0 && aux1 % 16 == 0) {
          fluxoBrutoDeBits[aux2] = Integer.parseInt(imp, 2);
          aux2++;
          imp = "";
        }
        aux1++;
        y--;
      }
    }
    return fluxoBrutoDeBits;
  }

  /**
   * Metodo: camadaDeAplicacaoReceptora
   * Funcao: converte a mensagem que esta em codigo ASCII para char
   *
   * @param fluxoBrutoDeBits
   * @return void
   */
  private void camadaDeAplicacaoReceptora(int[] fluxoBrutoDeBits) {
    String mensagem = "";

    StringBuilder sb = new StringBuilder(fluxoBrutoDeBits.length);
    for (int i = 0; i < fluxoBrutoDeBits.length; ++i) {
      if (fluxoBrutoDeBits[i] != -1)
        sb.append((char) fluxoBrutoDeBits[i]);
    }

    mensagem = sb.toString();

    aplicacaoReceptora(mensagem);
  } // Fim do metodo camadaDeAplicacaoReceptora

  /**
   * Metodo: aplicacaoReceptora
   * Funcao: exibir o texto decodificado na Area de Texto "mensagemReceptor"
   *
   * @param mensagem eh a mensagem que foi decodificada
   * @return void
   */
  private void aplicacaoReceptora(String mensagem) {
    mensagemReceptor.setText(mensagem); // Seta a mensagem no JTextArea "mensagemReceptor"
  } // Fim do metodo aplicacaoReceptora

  /**
   * Metodo: addItem
   * Funcao: organizar a aplicacao ultilizando o GridBagLayout
   *
   * @param p
   * @param c
   * @param x
   * @param y
   * @param width
   * @param height
   * @param align
   * @return void
   */
  private void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align) {
    GridBagConstraints gc = new GridBagConstraints();
    gc.gridx = x;
    gc.gridy = y;
    gc.gridwidth = width;
    gc.gridheight = height;
    gc.weightx = 100.0;
    gc.weighty = 100.0;
    gc.insets = new Insets(5, 5, 5, 5);
    gc.anchor = align;
    gc.fill = GridBagConstraints.NONE;
    p.add(c, gc);
  } // Fim do metodo addItem
} // Fim da classe Principal
