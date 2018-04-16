----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    11:56:53 03/30/2018 
-- Design Name: 
-- Module Name:    MasterJoystick - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created
-- Additional Comments: 
--
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity MasterJoystick is
    Port ( clk : in  STD_LOGIC;
           reset : in  STD_LOGIC;
			  en :  in STD_LOGIC;
			  busy : out STD_LOGIC;
           sclk : out  STD_LOGIC;
           mosi : out  STD_LOGIC;
			  miso : in STD_LOGIC;
           ss : out  STD_LOGIC;
           leds : in  STD_LOGIC_VECTOR (1 downto 0);
           buttons : out  STD_LOGIC_VECTOR (1 downto 0);
           jsButton : out  STD_LOGIC;
           xAxis : out  STD_LOGIC_VECTOR (9 downto 0);
           yAxis : out  STD_LOGIC_VECTOR (9 downto 0));
end MasterJoystick;

architecture Behavioral of MasterJoystick is
	
	type MasterJoystickStatus is (
		masterRAZ, waitStart, er_STARTING, er_BUZY
	);
	type TransmissionIndex is (
		Byte1, Byte2, Byte3, Byte4, Byte5
	);

	COMPONENT er_1octet
	PORT(
		en : IN std_logic;
		din : IN std_logic_vector(7 downto 0);
		clk : IN std_logic;
		reset : IN std_logic;
		miso : IN std_logic;          
		dout : OUT std_logic_vector(7 downto 0);
		busy : OUT std_logic;
		sclk : OUT std_logic;
		mosi : OUT std_logic
		);
	END COMPONENT;
	
	COMPONENT sub_clock
	generic ( clock_length : natural := 8);
	PORT(
		clk : IN std_logic;
		RAZ : IN std_logic;          
		out_clk : OUT std_logic
		);
	END COMPONENT;
	
	signal er_buzy : std_logic;
	signal er_en : std_logic;
	signal er_din : std_logic_vector(7 downto 0);
	signal er_dout : std_logic_vector(7 downto 0);
	-- mémoire des valeur d'entré au cas où elles changerait a l'extérieur
	signal mem_leds : std_logic_vector(1 downto 0);
	
	-- a 1 MHz clock based on a 100MHz clock (clk) 
	signal slowclk : std_logic;
begin
	Inst_er_1octet: er_1octet PORT MAP(
		en => en_er,
		din => er_din,
		dout => er_dout,
		busy => buzy_er,
		clk => slowclk, -- 1 MHz clock
		reset => reset,
		sclk => sclk,
		mosi => mosi,
		miso => miso
	);
	
	-- Generate slow_clk (1 MHz) from clk (100 MHz)
	Inst_sub_clock: sub_clock
	GENERIC MAP (100)
	PORT MAP(
		clk => clk,
		RAZ => reset,
		out_clk => slow_clk
	);
	
	master : process (clk, reset)
		variable state : MasterJoystickStatus;
		variable trIndex : TransmissionIndex;
		variable cpt_init : Natural;
	begin
		if (reset = '1') then
			busy <= '0';
			ss <= '1';
			buttons <= (others => '0');
			jsButton <= '0';
			xAxis <= (others => '0');
			yAxis <= (others => '0');
			er_din <= (others => '0');
			mem_leds <= (others => '0');
			state <= masterRAZ;
			trIndex := Byte1;
		elsif (rising_edge(clk)) then
			case state is
				when masterRAZ =>
					if (en = '1') then
						ss <= '0';
						busy <= '1';
						mem_leds <= leds;
						state := waitStart;
						cpt_init := 1500;
						trIndex := Byte1;
					end if;
				when waitStart =>
					-- wait for ~ 15us, (1500 tick for a 100MHz clock)
					if (cpt_init <= 0) then
						-- Start communication
						-- Send led command
						er_din(7 downto 2) <= "100000";
						er_din(1 downto 0) <= mem_leds;
						-- Start er_1octet
						er_en <= '1';
						state := er_STARTING;
					else
						cpt_init := cpt_init - 1;
					end if;
				when er_STARTING => 
					-- ensure er_1octet start
					if (er_busy ='1') then 
						state := er_BUZY;
						if (trIndex = Byte5) then
							-- stop er_1octet after this transmission
							er_en <= '0';
						end if;
					end if;
				when er_BUZY =>
					-- wait end of er_1octet
					if (er_busy ='0') then
						case trIndex is
							when Byte1 =>
								-- first byte received X(low)
								xAzis(7 downto 0) <= er_dout;
								trIndex := Byte2;
							when Byte2 =>
								-- second byte received X(high)
								xAzis(9 downto 8) <= er_dout(1 downto 0);
								trIndex := Byte3;
							when Byte3 =>
								-- third byte received Y(low)
								yAzis(7 downto 0) <= er_dout;
								trIndex := Byte4;
							when Byte4 =>
								-- fouth byte received Y(high)
								yAzis(9 downto 8) <= er_dout(1 downto 0);
								trIndex := Byte5;
							when Byte5 =>
								-- fifth byte received buttons
								buttons <= er_dout(2 downto 1);
								jsButton <= er_dout(0);
						end case;
						if (trIndex = Byte5) then
							-- all data have been exanged
							state := masterSTOP;
						else
							-- still data to send
							state := er_STARTING;
						end if;
					end if;
				when masterSTOP => 
					-- reset the componnent
					ss <= '1';
					buzy <= '0';
					er_en <= '0';
					state := masterRAZ;
			end case;
		end if;
	end process;
end Behavioral;

