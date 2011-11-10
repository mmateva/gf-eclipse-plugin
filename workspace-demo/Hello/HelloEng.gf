concrete HelloEng of HelloAbs = ResEng ** { 
--concrete HelloEng of HelloAbs = open (Alias = ResEng) in {
	
	flags
		coding = utf8 ;
 
	lincat
		Greeting, Farewell = {s : Str} ;
		Recipient = {s : Param => Str} ; 

	lin
 		Hello recip = {s = "hello" ++ recip.s ! Masc } ; 
		Goodbye recip = {s = "goodbye" ++ recip.s ! Fem } ;
		
		World = {s = \\_ => "world"} ;
		Parent = { s = table {
			Masc => "dad" ; Fem => "mum"
		} } ;
		Friends = mega "friends" "loved ones" ;

	oper
		superate : Str -> Recipient = \s ->
			lin Recipient { s = \\_ => "super" ++ s } ;
 
		mega : Recipient = overload {
			mega : Str -> Recipient = \s ->
				lin Recipient { s = \\_ => "mega" ++ s } ;
				
			mega : Str -> Str -> Recipient = \s,r ->
				lin Recipient { s = \\_ => "mega" ++ s ++ "and" ++ "mega" ++ r }
		} ;
} ;