library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

entity Nexys4 is
  port (
    -- les 16 switchs
    swt : in std_logic_vector (15 downto 0);
    -- les anodes pour sélectionner l'afficheur 7 segments
    an : out std_logic_vector (7 downto 0);
    -- afficheur 7 segments (point décimal compris, segment 7)
    ssg : out std_logic_vector (7 downto 0);
    -- horloge
    mclk : in std_logic;
    -- les 5 boutons noirs
    btnC, btnU, btnL, btnR, btnD : in std_logic;
    -- les 16 leds
    led : out std_logic_vector (15 downto 0);
	 
	 -- le Pmod JA
	 misoJA1,  misoJA2 : in std_logic;
	 ssJA1, mosiJA1, sclkJA1, ssJA2, mosiJA2, sclkJA2 : out std_logic
  );
  
end Nexys4;

architecture synthesis of Nexys4 is
  -- rappel du (des) composant(s)
  COMPONENT MasterJoystick
	PORT(
		clk : IN std_logic;
		reset : IN std_logic;
		en : IN std_logic;
		miso : IN std_logic;
		leds : IN std_logic_vector(1 downto 0);          
		busy : OUT std_logic;
		sclk : OUT std_logic;
		mosi : OUT std_logic;
		ss : OUT std_logic;
		buttons : OUT std_logic_vector(1 downto 0);
		jsButton : OUT std_logic;
		xAxis : OUT std_logic_vector(9 downto 0);
		yAxis : OUT std_logic_vector(9 downto 0)
		);
	END COMPONENT;
	
	-- 7segment dispay
	COMPONENT dec10to7seg
	PORT(
		value1 : IN std_logic_vector(9 downto 0);
		value2 : IN std_logic_vector(9 downto 0);
		reset : IN std_logic;          
		ssg : OUT std_logic_vector(7 downto 0);
		an : OUT std_logic_vector(7 downto 0)
		);
	END COMPONENT;
	
	signal ss : std_logic;
	signal miso : std_logic;
	signal mosi : std_logic;
	signal sclk : std_logic;
	
	signal value7seg1 : std_logic_vector(9 downto 0);
	signal value7seg2 : std_logic_vector(9 downto 0);
begin
	-- Map pmod IO
	miso <= misoJA1 or misoJA2;
	ssJA1 <= ss;
	ssJA2 <= ss;
	mosiJA1 <= mosi;
	mosiJA2 <= mosi;
	sclkJA1 <= sclk;
	sclkJA2 <= sclk;
	
	Inst_dec10to7seg: dec10to7seg PORT MAP(
		value1 => value7seg1,
		value2 => value7seg2,
		ssg => ssg,
		an => an,
		reset => nbtnC 
	);
	
  -- convention afficheur 7 segments 0 => allumé, 1 => éteint
  --ssg <= (others => '1');
  -- aucun afficheur sélectionné
  --an(7 downto 0) <= (others => '1');
  -- 16 leds éteintes
  led(15 downto 0) <= (others => '0');

  -- connexion du (des) composant(s) avec les ports de la carte
  Inst_MasterJoystick: MasterJoystick PORT MAP(
		clk => mclk,
		reset => nbtnC,-- Boutton du centre pour reset le composant
		en => nbtnD,   -- Boutton du bas pour demander un echange
		busy => led(0), -- Led de droite pour afficher busy
		sclk => sclk,
		mosi => mosi,
		miso => miso,
		ss => ss,
		leds => sw(1 downto 0), -- les switch de droite commandent les leds
		buttons => led(14 downto 13),
		jsButton => led(15), -- afficher les boutons sur les led de gauche
		xAxis => value7seg1,
		yAxis => value7seg2
	);
    
end synthesis;
