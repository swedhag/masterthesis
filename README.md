# Master Thesis
This is the source code repository of my Master Thesis project.

To generate order-constrained trees, firstly install [Inviwo](https://inviwo.org/) and add Weibke and Weinkauf's [Temporal Treemaps](https://github.com/Wiebke/TemporalTreeMaps) implementation as an external module. Installation instructions can be found [here](https://github.com/Wiebke/TemporalTreeMaps). Swap the expression <code>jNodes[std::to_string(idxNode)] = jSingleNode;</code> to <code>jNodes[std::to_string(idxNode) + "_" + ThisNode.name] = jSingleNode;</code> of *treewriter.cpp* to generate NTG files viable with my code.

In the Master Thesis, Sondag et al.'s [IncrementalTreemap](https://gitaga.win.tue.nl/max/IncrementalTreemap) has been used for animations of topologically evolving trees. Add the contents of this repository to a local version of their software, before modifying the *src/UserControl/Visualiser/Visualiser.java* and *src/UserControl/Visualiser/GUI.java* to  retrieve the animated results.

The report relating to the project can be found [here](https://www.diva-portal.org/smash/search.jsf?dswid=-7909), search for "Svedhag."
