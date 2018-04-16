----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    04:43:00 04/03/2018 
-- Design Name: 
-- Module Name:    dec10to7seg - Behavioral 
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

entity dec10to7seg is
    Port ( value1 : in  STD_LOGIC_VECTOR (9 downto 0);
           value2 : in  STD_LOGIC_VECTOR (9 downto 0);
			  ssg : out  STD_LOGIC_VECTOR (7 downto 0);
           an : out  STD_LOGIC_VECTOR (7 downto 0);
           reset : in std_logic);
end dec10to7seg;

architecture Behavioral of dec10to7seg is
	
	COMPONENT sub_clock
	generic ( clock_length : natural := 8);
	PORT(
		clk : IN std_logic;
		RAZ : IN std_logic;          
		out_clk : OUT std_logic
		);
	END COMPONENT;
	
	COMPONENT dec7seg
	PORT(
		value : IN std_logic_vector(3 downto 0);          
		seg : OUT std_logic_vector(7 downto 0)
		);
	END COMPONENT;
	
	
	signal switch_clock : std_logic; -- a slow clock (100hz) for switch the displayed digit
	signal error_signal : std_logic;
	signal current_value : std_logic_vector(3 downto 0);
begin

	-- Generate slow_clk (100 Hz) from clk (100 MHz)
	Inst_sub_clock: sub_clock
	GENERIC MAP (1000000)
	PORT MAP(
		clk => clk,
		RAZ => reset,
		out_clk => switch_clock
	);
	
	Inst_dec7seg: dec7seg PORT MAP(
		value => current_value,
		seg => ssg
	);

	
	-- Processus chargé de chargé de faire tourner les anodes et la valeur envoyée a dec7seg
	display : process(switch_clock, reset)
		variable current_displayed : Natural;
	begin
		if (reset = '1') then
			current_displayed := 7;
		elsif (rising_edge(switch_clock)) then
			
			-- Changer la valeur du 7 segment anodes des 7 segments 
			case current_displayed is
				when 0 => 
					-- poids faible de value1
					current_value <= value1(3 downto 0);
				when 1 =>
					-- poids moyen de value1
					current_value <= value1(7 downto 4);
				when 2 => 
					-- poids fort de value1
					current_value(1 downto 0) <= value1(9 downto 8);
					current_value(7 downto 2) <= (others => '0');
				when 3 =>
					current_value <= (others => '0');
				when 4 => 
					-- poids faible de value2
					current_value <= value2(3 downto 0);
				when 5 =>
					-- poids moyen de value2
					current_value <= value2(7 downto 4);
				when 6 =>
					-- poids fort de value2
					current_value(1 downto 0) <= value2(9 downto 8);
					current_value(7 downto 2) <= (others => '0');
				when 7 =>
					current_value <= (others => '0');
				when others => 
					-- erreur
					error_signal <= '1'; --  TODO mieux vaut un type adapté 
			end case;

			-- Changer les anodes des 7 segments 
			if (current_displayed = 7) then
				-- allumer l'anode courrante
				an(7) <= '0'; 
				-- eteindre la precedente anode
				an(0) <= '1';
				-- changer l'anode courrante
				current_displayed := 6;	
			else	
				-- allumer l'anode courrante
				an(current_displayed) <= '0'; 
				-- eteindre la precedente anode
				an(current_displayed + 1) <= '1';
				-- changer l'anode courrante
				current_displayed := current_displayed - 1;
			end if;
			
			
		end if;
	end process;

end Behavioral;

