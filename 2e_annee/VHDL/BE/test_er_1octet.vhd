--------------------------------------------------------------------------------
-- Company: 
-- Engineer:
--
-- Create Date:   09:01:07 03/23/2018
-- Design Name:   
-- Module Name:   /home/sdeneuvi/Documents/2A/vhdl/projet/tets_er_1octet.vhd
-- Project Name:  projet
-- Target Device:  
-- Tool versions:  
-- Description:   
-- 
-- VHDL Test Bench Created by ISE for module: er_1octet
-- 
-- Dependencies:
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
--
-- Notes: 
-- This testbench has been automatically generated using types std_logic and
-- std_logic_vector for the ports of the unit under test.  Xilinx recommends
-- that these types always be used for the top-level I/O of a design in order
-- to guarantee that the testbench will bind correctly to the post-implementation 
-- simulation model.
--------------------------------------------------------------------------------
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;
 
-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--USE ieee.numeric_std.ALL;
 
ENTITY test_er_1octet IS
END test_er_1octet;
 
ARCHITECTURE behavior OF test_er_1octet IS 
 
    -- Component Declaration for the Unit Under Test (UUT)
 
    COMPONENT er_1octet
    PORT(
         en : IN  std_logic;
         din : IN  std_logic_vector(7 downto 0);
         dout : OUT  std_logic_vector(7 downto 0);
         busy : OUT  std_logic;
         clk : IN  std_logic;
         reset : IN  std_logic;
         sclk : OUT  std_logic;
         mosi : OUT  std_logic;
         miso : IN  std_logic
         );
    END COMPONENT;
    
	COMPONENT compteu_simple
    PORT(
         clk : IN  std_logic;
         RAZ : IN  std_logic;
         cpt : OUT  std_logic_vector(7 downto 0);
         carry_out : OUT  std_logic
        );
    END COMPONENT;

   --Inputs
   signal en : std_logic := '0';
   signal din : std_logic_vector(7 downto 0) := (others => '0');
   signal clk : std_logic := '0';
   signal reset : std_logic := '0';
   signal miso : std_logic := '0';

 	--Outputs
   signal dout : std_logic_vector(7 downto 0);
   signal busy : std_logic;
   signal sclk : std_logic;
   signal mosi : std_logic;
	
	-- signaux pour le compteur
	signal not_reset : std_logic;
	signal carry_out1 : std_logic;
	signal cpt1 : std_logic_vector(7 downto 0);

	-- signal pour le debug
	signal error_signal : std_logic;
	
   -- Clock period definitions
   constant clk_period : time := 10 ns;
	

BEGIN
 
	-- Instantiate the Unit Under Test (UUT)
   uut: er_1octet PORT MAP (
          en => en,
          din => din,
          dout => dout,
          busy => busy,
          clk => clk,
          reset => reset,
          sclk => sclk,
          mosi => mosi,
          miso => miso
         );

	-- le compteur sert a obtenir une valeur semi-aléatoire pour les tests
	compteur1: compteu_simple PORT MAP (
          clk => clk,
          RAZ => not_reset,
          cpt => cpt1,
          carry_out => carry_out1
        );

	not_reset <= not reset;
	-- Process chargé de placer en et din
	manage_en_din : process
		variable  cpt_limit : natural := 0;
		variable	 nb_test : natural;
	begin
		error_signal <= '0';
		reset <= '1';
		nb_test := 0;
		wait for 100 ns;
		reset <= '0';
		
		-- insert stimulus here 
		while (nb_test < 5 and cpt_limit < 1000) loop
			-- limite arbitraire pour ne pas boucler a l'infini en cas d'erreur
			cpt_limit := cpt_limit + 1;
			-- attendre la fin du test
			if (busy = '1') then
				en <= '0';
			else
				-- placer din
				case nb_test is
					when 0 => 	din <= "11111111"; -- test 1
					when 1 => 	din <= "10101010"; -- test 2
					when 2 => 	din <= "10011001"; -- test 3
					when 3 => 	din <= "11001100"; -- test 4
					when 4 => 	din <= "00000000"; -- recupérer la valeur du dernier test
					when others => error_signal <= '1';
				end case;
				en <= '1';
				nb_test := nb_test + 1;		
				wait for clk_period*2; -- attendre le temps que busy repasse à 1
			end if;
			wait for clk_period;
		end loop;
			en <= '0';
      wait;
	end process;

	-- Process chargé de placer miso
	manage_miso : process
	begin
		wait for 100 ns;
		--while nb_test < 5 loop
		for i in 0 to 200 loop
			miso <= cpt1(1) xor cpt1(3) xor cpt1(5);
			wait for 5 ns;
		end loop;
		wait;
	end process;
   -- Clock process definitions
   clk_process :process
   begin
		clk <= '0';
		wait for clk_period/2;
		clk <= '1';
		wait for clk_period/2;
   end process;

END behavior;
