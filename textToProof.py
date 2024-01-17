import networkx as nx
import matplotlib.pyplot as plt
import graphviz
import sys

arguments = sys.argv[1:]
filename = arguments[0]
print(arguments)

G = nx.read_edgelist(f"{filename}.txt", delimiter=":")

nodes = G.nodes()
edges = G.edges()
    
proof = graphviz.Digraph(comment = "Proof")

for node in nodes:
    proof.node(node, f"Node: {node}")
    
for index, edge in enumerate(edges):
    proof.edge(edge[0], edge[1], f"Edge {index}")
    
proof.render(f"{filename}", format="png", cleanup=True)



