--# -path=.:/home/john/.cabal/share/gf-3.3/lib/alltenses
resource VPConj = open ResEng, Coordination in {

oper
  ListVP = {s1,s2 : VForm => Agr => Str} ;
  
  BaseVP : VP -> VP -> ListVP = \vp1,vp2 ->
    twoTable2 VForm Agr (mkVPI vp1) (mkVPI vp2) ;
  
  ConsVP : VP -> ListVP -> ListVP = \vp,vps ->
    consrTable2 VForm Agr comma (mkVPI vp) vps ;

  ConjVP : {s1,s2 : Str} -> ListVP -> VP = \conj,vps ->
    vpi2vp (conjunctDistrTable2 VForm Agr conj vps) ;

  VPI = {s : VForm => Agr => Str} ;
  
  mkVPI : VP -> VPI ;
  mkVPI vp = {
      s = \\v,a =>  case v of {
                        VInf      => vp.ad ++ 
                                     vp.inf ;
                        VPres     => let verb = vp.s ! Pres ! Simul ! CPos ! ODir ! a
                                     in verb.aux ++ verb.adv ++ 
                                        vp.ad ++
                                        verb.fin ++ verb.inf ;
                        VPPart    => vp.ad ++ 
                                     vp.ptp ;
                        VPresPart => vp.ad ++ 
                                     vp.prp ;
                        VPast     => let verb = vp.s ! Past ! Simul ! CPos ! ODir ! a
                                     in verb.aux ++ verb.adv ++ 
                                        vp.ad ++ 
                                        verb.fin ++ verb.inf
                      } ++
                      vp.s2 ! a
      } ;

  vpi2vp : {s : VForm => Agr => Str} -> VP ;
  vpi2vp vpi = {
    s = \\t,ant,b,ord,agr => 
      let
        inf  = vpi.s ! VInf   ! agr ;
        fin  = vpi.s ! VPres  ! agr ;
        part = vpi.s ! VPPart ! agr ;
        past = vpi.s ! VPast  ! agr ;
      in
      case <t,ant,b,ord> of {
        <Pres,Simul,CPos,ODir>   => vff             fin [] ;
        <Pres,Simul,CPos,OQuest> => vf (does agr)   inf ;
        <Pres,Anter,CPos,_>      => vf (have agr)   part ; --# notpresent
        <Pres,Anter,CNeg c,_>    => vfn c (have agr) (havent agr) part ; --# notpresent
        <Past,Simul,CPos,ODir>   => vff past [] ; --# notpresent
        <Past,Simul,CPos,OQuest> => vf "did"        inf ; --# notpresent
        <Past,Simul,CNeg c,_>    => vfn c "did" "didn't"     inf ; --# notpresent
        <Past,Anter,CPos,_>      => vf "had"        part ; --# notpresent
        <Past,Anter,CNeg c,_>    => vfn c "had" "hadn't"     part ; --# notpresent
        <Fut, Simul,CPos,_>      => vf "will"       inf ; --# notpresent
        <Fut, Simul,CNeg c,_>    => vfn c "will" "won't"      inf ; --# notpresent
        <Fut, Anter,CPos,_>      => vf "will"       ("have" ++ part) ; --# notpresent
        <Fut, Anter,CNeg c,_>    => vfn c "will" "won't"("have" ++ part) ; --# notpresent
        <Cond,Simul,CPos,_>      => vf "would"      inf ; --# notpresent
        <Cond,Simul,CNeg c,_>    => vfn c "would" "wouldn't"   inf ; --# notpresent
        <Cond,Anter,CPos,_>      => vf "would"      ("have" ++ part) ; --# notpresent
        <Cond,Anter,CNeg c,_> => vfn c "would" "wouldn't" ("have" ++ part) ; --# notpresent
        <Pres,Simul,CNeg c,_>    => vfn c (does agr) (doesnt agr) inf
        } ;
    prp  = vpi.s ! VPresPart ! AgP3Pl ;
    ptp  = vpi.s ! VPPart ! AgP3Pl ;
    inf  = vpi.s ! VInf ! AgP3Pl ;
    ad   = [] ;
    s2 = \\a => []
    } ;

}
