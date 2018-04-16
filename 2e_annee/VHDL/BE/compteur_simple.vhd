----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    10:27:35 02/19/2018 
-- Design Name: 
-- Module Name:    compteu_simple - Behavioral 
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
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity compteu_simple is
    Port ( clk : in  STD_LOGIC;
           RAZ : in  STD_LOGIC;
           cpt : out  STD_LOGIC_VECTOR (7 downto 0);
           carry_out : out  STD_LOGIC);
end compteu_simple;

architecture Behavioral of compteu_simple is
begin
	process (clk,RAZ) 
		variable cpt_aux : std_logic_vector(7 downto 0);
	begin
		if RAZ='0' then
			cpt_aux := (others => '0');
		else 
			if rising_edge(clk) then
				cpt_aux := cpt_aux + 1;
			end if;
		end if;
		cpt <= cpt_aux;
		if cpt_aux = 0 then
			carry_out <= '1';
		else
			carry_out <= '0';
		end if;
	end process;
end Behavioral;

