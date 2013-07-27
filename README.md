
# TI (_Technologies de l'Intelligence_ ou **Tecnologias da Inteligência**)

Este repositório está dedicado para compartilhar componentes de 
**TI** (_Technologies de l'Intelligence_ ou **Tecnologias da Inteligência**) 
desenvolvidos para nossas aplicações.

## componentes existentes

#### utilitário Java

  So you have lost your class? Find it back with: `java -jar [whereis.jar](http://cua.li/TI/whereis.jar) MyClass /in/MyApps`
  
  As this tool is making use of the temporary directory to expand the embedded archives, you may have to change the `java.io.tmpdir` property to a directory you have write permissions to.

```bash
java -Djava.io.tmpdir=D:\tmp -jar [whereis.jar](http://cua.li/TI/whereis.jar) SAXParserFactory D:\JBoss
```
  
  More explanations on [cua.li/TI](http://cua.li/TI/whereIsMyClass.html)'s site.

#### componentes ScalaFX

para uma interface gráfica mais orientada à manipulação direta

  + escolher uma cor entre 216 em células hexagonais organizadas por nuances
    (inspirado em [VisiBone](http://www.VisiBone.com/color/hexagon.html) de Bob Stein)
  + organizar uma sequência de nós gráficos em uma tira quase 3D
  + empilhar vários nós gráficos com um pequeno deslize de cada um
  + relógio com possibilidade de escolher ao vivo a cor dos ponteiros

#### componentes Java

para dar suporte aos componentes gráficos
  
  + empacotar uma pasta ou um arquivo dentro de um arquivo ZIP

## componentes planejados

#### componentes ScalaFX

  + escolher uma cor entre 1068 em células triangulares organizadas por nuances
    (inspirado em [VisiBone](http://www.VisiBone.com/color/kilochart.html) de Bob Stein)
  + escolher uma fonte numa tabela parecida com a tabela periódica dos elementos
    (inspirado em [Periodic Table of Typefaces](http://www.squidspot.com/Periodic_Table_of_Typefaces.html) de Camdon Wilde)
