----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    11:47:35 03/23/2018 
-- Design Name: 
-- Module Name:    MasterSum - Behavioral 
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
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity MasterSum is
    Port ( en : in  STD_LOGIC;
           e1 : in  STD_LOGIC_VECTOR (7 downto 0);
           e2 : in  STD_LOGIC_VECTOR (7 downto 0);
           s : out  STD_LOGIC_VECTOR (7 downto 0);
           carry : out  STD_LOGIC;
           busy : out  STD_LOGIC;
           mosi : out  STD_LOGIC;
           miso : in  STD_LOGIC;
           ss : out  STD_LOGIC;
           sclk : out  STD_LOGIC;
           clk : in  STD_LOGIC;
           reset : in  STD_LOGIC);
end MasterSum;

architecture Behavioral of MasterSum is

	type MasterStatus is (
		masterRAZ, waitSTART, waitPHASE1, PHASE1,  waitPHASE2, PHASE2 
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

	signal er_buzy : std_logic;
	signal er_en : std_logic;
	signal er_din : std_logic_vector(7 downto 0);
	signal er_dout : std_logic_vector(7 downto 0);
	-- mémoire des valeur d'entré au cas où elles changerait a l'extérieur
	signal mem_e1 : std_logic_vector(7 downto 0);
	signal mem_e2 : std_logic_vector(7 downto 0);
begin
	Inst_er_1octet: er_1octet PORT MAP(
			en => er_en,
			din => er_din,
			dout => er_dout,
			busy => er_buzy,
			clk => clk,
			reset => reset,
			sclk => sclk,
			mosi => mosi,
			miso => miso
		);

	master : process (clk, reset)
		variable state : MasterStatus;
		variable cpt_init : natural;
	begin
		if (reset = '1') then
			state := masterRAZ;
			er_en <= '0';
			er_din <= (others => '0');
			ss <= '1';
			carry <= '0';
			s <= (others => '0');
			mem_e1 <= (others => '0');
			mem_e2 <= (others => '0');
			busy <= '0';
		elsif (rising_edge(clk)) then
			case state is
				when masterRAZ =>
					-- Attendre enable (en)
					if (en = '1') then
						-- compteur pour attendre 3 ticks avant de commencer
						cpt_init := 3;
						-- mémoriser les valeur d'entrée
						mem_e1 <= e1;
						mem_e2 <= e2;
						-- démarrer la communication
						ss <= '0';
						busy <= '1';
						state := waitSTART;
					end if;
				when waitSTART => 
					-- laisser 3 tick supplémentaires pour que le slave se prépare
					cpt_init := cpt_init - 1 ;
					if (cpt_init <= 0) then
						-- envoyer e1
						er_din <= mem_e1;
						er_en <= '1';

						state := waitPHASE1;
					end if;
				when waitPHASE1 =>
					-- Attendre que er_1octet démmarre
					if (er_buzy = '1') then
						er_en <= '0';
						state := PHASE1;
					end if;
				when PHASE1 =>
					-- Attendre la fin de er_1octet
					if (er_buzy = '0') then
						-- recupérer s 
						s <= er_dout;
						-- envoyer e2
						er_en <= '1';
						er_din <= mem_e2;
						state := waitPHASE2;
					end if;
				when waitPHASE2 =>
					-- Attendre que er_1octet démmarre
					if (er_buzy = '1') then
						er_en <= '0';
						state := PHASE2;
					end if;
				when PHASE2 =>
				-- Attendre la fin de er_1octet
					if (er_buzy = '0') then
						-- recupérer carry
						carry <= er_dout(7);
						-- terminer la communication
						ss <= '1';
						busy <= '0';
						state := masterRAZ;
					end if;
			end case;
		end if;
	end process;
end Behavioral;

